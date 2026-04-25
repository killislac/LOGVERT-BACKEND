package com.logistica.doisv.services;

import com.logistica.doisv.dto.*;
import com.logistica.doisv.dto.registro_solicitacao.CriarSolicitacaoDTO;
import com.logistica.doisv.dto.registro_solicitacao.HistoricoSolicitacaoDTO;
import com.logistica.doisv.dto.registro_solicitacao.SolicitacaoDetalhadaDTO;
import com.logistica.doisv.dto.registro_solicitacao.SolicitacaoResumidaDTO;
import com.logistica.doisv.dto.registro_venda.requisicao.ItemDTO;
import com.logistica.doisv.dto.registro_venda.requisicao.RegistroVendaDTO;
import com.logistica.doisv.entities.*;
import com.logistica.doisv.entities.enums.CategoriaArquivoPermitida;
import com.logistica.doisv.entities.enums.Status;
import com.logistica.doisv.entities.enums.StatusSolicitacao;
import com.logistica.doisv.entities.enums.TipoSolicitacao;
import com.logistica.doisv.repositories.SolicitacaoRepository;
import com.logistica.doisv.repositories.VendaRepository;
import com.logistica.doisv.services.api.AnexoDriveService;
import com.logistica.doisv.services.api.GoogleDriveService;
import com.logistica.doisv.services.exceptions.ResourceNotFoundException;
import com.logistica.doisv.util.validacao.ArquivoValidador;
import com.logistica.doisv.util.validacao.SolicitacaoValidador;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SolicitacaoService {

    private final SolicitacaoRepository repository;
    private final VendaRepository vendaRepository;
    private final AnexoDriveService anexoService;
    private final SolicitacaoValidador validador;
    private final VendaService vendaService;
    private final ArquivoValidador arquivoValidador;

    @Transactional(readOnly = true)
    public Page<SolicitacaoResumidaDTO> buscarTodos(Pageable pageable, Long idLoja){
        return repository.listarSolicitacoesResumidas(pageable, idLoja);
    }

    @Transactional(readOnly = true)
    public SolicitacaoDetalhadaDTO buscarPorId(Long id, Long idLoja){
        Solicitacao solicitacao = repository.buscarCompletoPorId(id).orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada"));
        validador.validarLoja(solicitacao, idLoja);

        return new SolicitacaoDetalhadaDTO(solicitacao);
    }

    @Transactional
    public SolicitacaoResumidaDTO registrarSolicitacao(CriarSolicitacaoDTO dto, List<MultipartFile> anexos, Long idVenda) throws GeneralSecurityException, IOException {
        validarTipoAnexo(anexos);

        Venda venda = vendaRepository.buscarVendaPorId(idVenda).orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada"));
        validador.validarStatusVenda(venda, dto.tipo());

        TipoSolicitacao tipoSolicitacao = TipoSolicitacao.deString(dto.tipo());
        validador.validarPrazoSolicitacao(venda, tipoSolicitacao);

        ItemVenda itemVenda = buscarItemVendaPorId(dto.idItem(), venda.getItensVenda());
        validador.validarQuantidade(dto.quantidade(), itemVenda.getQuantidade(), dto.tipo());

        Solicitacao solicitacao = construirSolicitacao(dto, venda, itemVenda, tipoSolicitacao);
        solicitacao = repository.save(solicitacao);

        processarAnexos(anexos, solicitacao.getId());

        return new SolicitacaoResumidaDTO(solicitacao);
    }

    @Transactional
    public SolicitacaoResumidaDTO aprovarSolicitacao(Long id, Long idLoja) {
        Solicitacao solicitacao = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada"));

        validador.validarAprovacaoSolicitacao(solicitacao, idLoja);

        HistoricoSolicitacao historico = criarHistorico(StatusSolicitacao.APROVADA, solicitacao,
                String.format("Solicitação de %s aprovada!", solicitacao.getTipoSolicitacao().getDescricao().toLowerCase()));

        solicitacao.setStatusSolicitacao(StatusSolicitacao.APROVADA);

        ItemVenda itemVenda = buscarItemVendaPorId(solicitacao.getItemVenda().getId(), solicitacao.getVenda().getItensVenda());

        var novaQuantidade = itemVenda.getQuantidade() - solicitacao.getQuantidade();
        itemVenda.setQuantidade(novaQuantidade);

        ItemVenda novoItemSolicitacao = ItemVenda.builder()
                .precoOriginal(itemVenda.getPrecoOriginal())
                .precoVendido(itemVenda.getPrecoVendido())
                .percentualVariacao(itemVenda.getPercentualVariacao())
                .quantidade(solicitacao.getQuantidade())
                .detalhes(String.format("Item para %s - Solicitação número: %d", solicitacao.getTipoSolicitacao().getDescricao(), solicitacao.getId()))
                .status(Status.INATIVO)
                .venda(solicitacao.getVenda())
                .produto(itemVenda.getProduto())
                .build();

        solicitacao.getVenda().getItensVenda().add(novoItemSolicitacao);
        solicitacao.getHistoricos().add(historico);
        solicitacao.setDataAtualizacao(historico.getDataAtualizacao());
        solicitacao.setItemVenda(novoItemSolicitacao);

        return new SolicitacaoResumidaDTO(repository.save(solicitacao));
    }

    @Transactional
    public SolicitacaoResumidaDTO reprovarSolicitacao(Long id, Long idLoja) throws GeneralSecurityException, IOException {
        Solicitacao solicitacao = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada"));

        validador.validarAprovacaoSolicitacao(solicitacao, idLoja);

        HistoricoSolicitacao historico = criarHistorico(StatusSolicitacao.REJEITADA, solicitacao,
                String.format("Solicitação de %s reprovada!", solicitacao.getTipoSolicitacao().getDescricao().toLowerCase()));

        solicitacao.getHistoricos().add(historico);
        solicitacao.setStatusSolicitacao(StatusSolicitacao.REJEITADA);
        solicitacao.setStatus(Status.INATIVO);
        solicitacao.setDataAtualizacao(historico.getDataAtualizacao());

        excluirAnexos(solicitacao);

        return new SolicitacaoResumidaDTO(repository.save(solicitacao));
    }

    @Transactional
    public SolicitacaoResumidaDTO editarSolicitacao(Long id, CriarSolicitacaoDTO dto, List<MultipartFile> anexos, Long idLoja) throws GeneralSecurityException, IOException {
        validarTipoAnexo(anexos);

        Solicitacao solicitacao = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada"));

        validador.validarAprovacaoSolicitacao(solicitacao, idLoja);
        validador.validarStatusVenda(solicitacao.getVenda(), solicitacao.getTipoSolicitacao().getDescricao().toLowerCase());
        validador.validarPrazoSolicitacao(solicitacao.getVenda(), solicitacao.getTipoSolicitacao());
        validador.validarQuantidade(dto.quantidade(), solicitacao.getQuantidade(), solicitacao.getTipoSolicitacao().getDescricao().toLowerCase());

        TipoSolicitacao tipoSolicitacao = TipoSolicitacao.deString(dto.tipo());

        ItemVenda itemVenda = buscarItemVendaPorId(dto.idItem(), solicitacao.getVenda().getItensVenda());

        solicitacao.setItemVenda(itemVenda);
        solicitacao.setQuantidade(dto.quantidade());
        solicitacao.setTipoSolicitacao(tipoSolicitacao);
        solicitacao.setMotivo(dto.motivo());

        solicitacao = repository.save(solicitacao);

        processarAnexos(anexos, solicitacao.getId());

        return new SolicitacaoResumidaDTO(solicitacao);
    }

    @Transactional
    public SolicitacaoDetalhadaDTO atualizarSolicitacao(Long idSolicitacao, HistoricoSolicitacaoDTO dto, Long idLoja, List<ItemDTO> novosProdutos) throws MessagingException {
        Solicitacao solicitacao = repository.findById(idSolicitacao).orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada"));

        validador.validarStatusVenda(solicitacao.getVenda(), solicitacao.getTipoSolicitacao().getDescricao().toLowerCase());
        validador.validarAtualizacao(solicitacao, idLoja);

        HistoricoSolicitacao historico = criarHistorico(StatusSolicitacao.deString(dto.statusNovo()), solicitacao, dto.observacao());

        solicitacao.setStatusSolicitacao(historico.getStatusAtual());
        solicitacao.getHistoricos().add(historico);

        solicitacao = repository.save(solicitacao);
        gerarVendaAposTroca(solicitacao, novosProdutos);

        return new SolicitacaoDetalhadaDTO(solicitacao);
    }


    @Transactional
    public SolicitacaoResumidaDTO cancelarSolicitacao(Long idSolicitacao, CriarSolicitacaoDTO dto, Long idLoja) throws GeneralSecurityException, IOException {
        Solicitacao solicitacao = repository.findById(idSolicitacao).orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada"));

        validador.validarCancelamento(solicitacao, idLoja);

        ItemVenda itemVenda = buscarItemVendaPorId(dto.idItem(), solicitacao.getVenda().getItensVenda());

        HistoricoSolicitacao historico = criarHistorico(StatusSolicitacao.CANCELADA, solicitacao,
                String.format("Solicitação cancelada - %s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));

        solicitacao.getHistoricos().add(historico);

        itemVenda.setStatus(Status.ATIVO);
        itemVenda.setDetalhes("");
        solicitacao.setStatusSolicitacao(historico.getStatusAtual());
        solicitacao.setStatus(Status.INATIVO);

        excluirAnexos(solicitacao);

        return new SolicitacaoResumidaDTO(repository.save(solicitacao));
    }


    private ItemVenda buscarItemVendaPorId(Long id, List<ItemVenda> itens){
        return itens.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado"));
    }

    private void processarAnexos(List<MultipartFile> anexos, Long idSolicitacao) throws GeneralSecurityException, IOException {
        if (anexos == null || anexos.isEmpty()) {
            return;
        }

        List<ArquivoDTO> arquivos = anexos.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(file -> {
                    try {
                        return new ArquivoDTO(file.getBytes(), file.getOriginalFilename(), file.getContentType());
                    } catch (IOException e) {
                        throw new RuntimeException("Erro ao ler arquivo: " + file.getOriginalFilename(), e);
                    }
                })
                .toList();

        if (!arquivos.isEmpty()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        anexoService.processarUploadAnexos(arquivos, idSolicitacao);
                    } catch (Exception e) {
                        throw new RuntimeException("Erro ao processar anexos: " + e.getMessage(), e);
                    }
                }
            });
        }
    }

    private void excluirAnexos(Solicitacao solicitacao) throws GeneralSecurityException, IOException {
        List<String> idsAnexos = solicitacao.getAnexos().stream()
                .map(anexo -> solicitacao.getId() + "_" + anexo.getId()).toList();

        solicitacao.getAnexos().clear();

        anexoService.processarExclusaoAnexos(idsAnexos, solicitacao.getClass().getSimpleName());
    }

    private Solicitacao construirSolicitacao(CriarSolicitacaoDTO dto, Venda venda, ItemVenda itemVenda, TipoSolicitacao tipo) {
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setVenda(venda);
        solicitacao.setItemVenda(itemVenda);
        solicitacao.setConsumidor(venda.getConsumidor());
        solicitacao.setTipoSolicitacao(tipo);
        solicitacao.setQuantidade(dto.quantidade());
        solicitacao.setMotivo(dto.motivo());
        solicitacao.setDataSolicitacao(Instant.now());
        return solicitacao;
    }

    private HistoricoSolicitacao criarHistorico(StatusSolicitacao statusNovo, Solicitacao solicitacao, String observacao){
        return HistoricoSolicitacao.builder()
                .statusAnterior(solicitacao.getStatusSolicitacao())
                .statusAtual(statusNovo)
                .observacao(observacao)
                .solicitacao(solicitacao)
                .dataAtualizacao(LocalDateTime.now())
                .build();
    }

    private void gerarVendaAposTroca(Solicitacao solicitacao, List<ItemDTO> novosProdutos) throws MessagingException {
        var solicitacaoFinalizada = solicitacao.getStatusSolicitacao() == StatusSolicitacao.CONCLUIDA;
        var temProdutosParaTroca = novosProdutos != null && !novosProdutos.isEmpty();

        if(solicitacaoFinalizada && temProdutosParaTroca && solicitacao.getTipoSolicitacao() == TipoSolicitacao.TROCA){
            vendaService.salvar(new RegistroVendaDTO(solicitacao, novosProdutos), solicitacao.getVenda().getLoja().getIdLoja());
        }
    }

    private void validarTipoAnexo(List<MultipartFile> anexos){
        if (anexos == null || anexos.isEmpty()) {
            return;
        }

        Set<CategoriaArquivoPermitida> categoriasPermitidas =
                Set.of(CategoriaArquivoPermitida.IMAGEM, CategoriaArquivoPermitida.VIDEO);

        anexos.forEach(arquivo -> arquivoValidador.validarOpcional(arquivo, categoriasPermitidas));
    }
}
