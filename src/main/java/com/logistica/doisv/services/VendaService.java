package com.logistica.doisv.services;

import com.logistica.doisv.dto.registro_venda.requisicao.ItemDTO;
import com.logistica.doisv.dto.registro_venda.requisicao.RegistroVendaDTO;
import com.logistica.doisv.dto.registro_venda.resposta.VendaDTO;
import com.logistica.doisv.dto.registro_venda.resposta.VendaResumidaDTO;
import com.logistica.doisv.entities.*;
import com.logistica.doisv.entities.enums.Status;
import com.logistica.doisv.entities.enums.StatusPedido;
import com.logistica.doisv.repositories.ConsumidorRepository;
import com.logistica.doisv.repositories.ProdutoRepository;
import com.logistica.doisv.repositories.VendaRepository;
import com.logistica.doisv.services.exceptions.EdicaoNaoPermitidaException;
import com.logistica.doisv.services.exceptions.RegraNegocioException;
import com.logistica.doisv.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class VendaService {

    @Autowired
    private VendaRepository repository;

    @Autowired
    private ConsumidorRepository consumidorRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private EmailService emailService;

    @Transactional(readOnly = true)
    public Page<VendaResumidaDTO> buscarTodasVendasPorLoja(Pageable pageable, Long idLoja){
        return repository.findVendasResumidasByLojaId(pageable, idLoja);
    }

    @Transactional(readOnly = true)
    public VendaDTO buscarPorId(Long idVenda, Long idLoja){
        Venda venda = repository.findByIdAndLojaIdLoja(idVenda, idLoja)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada"));

        return new VendaDTO(venda);
    }

    @Transactional
    public VendaDTO salvar(RegistroVendaDTO dto, Long idLoja){
        Consumidor consumidor = consumidorRepository.buscarConsumidorAtivo(dto.idConsumidor(), idLoja)
                .orElseThrow(() -> new ResourceNotFoundException("Consumidor não encontrado"));

        Loja loja = consumidor.getLoja();
        Venda venda = new Venda(loja, consumidor, dto.statusPedido(), dto.desconto(), dto.formaPagamento(), dto.prazoTroca(), dto.prazoDevolucao());

        calcularValorVenda(venda, dto, loja);

        gerarAcessoConsumidor(venda);

        venda = repository.save(venda);

        return new VendaDTO(venda);
    }

    @Transactional
    public VendaDTO atualizar(Long idVenda, RegistroVendaDTO dto, Long idLoja){
        Venda venda = buscarVendaParaAtualizacao(idVenda, idLoja);

        validarPermissaoEdicao(venda);
        atualizarConsumidorSeNecessario(venda, dto.idConsumidor(), idLoja);
        atualizarDadosDaVenda(venda, dto);

        Map<Long, Double> itensPreExistentes = mapearItensPreExistentes(venda);
        recalcularItensDaVenda(venda, dto, itensPreExistentes);

        venda = repository.save(venda);
        gerarAcessoConsumidor(venda);

        return new VendaDTO(venda);
    }

    @Transactional
    public void inativar(List<Long> idsVendas, Long idLoja){
        List<StatusPedido> statusBloqueadosParaInativar = List.of(StatusPedido.ENTREGUE, StatusPedido.CANCELADA);

        int quantidadeVendasInativadas = repository.atualizarStatusVendas(idsVendas, idLoja, Status.INATIVO,
                statusBloqueadosParaInativar);

        if(quantidadeVendasInativadas <= 0){
            throw new RegraNegocioException("Nenhuma venda elegível para inativação. " +
                    "Vendas com status ENTREGUE ou CANCELADA não podem ser inativadas.");
        }
    }

    private void gerarAcessoConsumidor(Venda venda){
        if(venda.getStatusPedido() == StatusPedido.ENTREGUE){
            venda.setSerialVenda(UUID.randomUUID().toString().substring(0,11).replace("-", ""));
            var senha = venda.getConsumidor().getCpf_cnpj().substring(0,4) + "@" + LocalDate.now().getYear();
            venda.setSenha(encoder.encode(senha));
            venda.setDataEntrega(LocalDate.now());

            enviarAcessoConsumidor(venda, senha);
        }
        else if(venda.getStatusPedido() == StatusPedido.CANCELADA){
            venda.setSerialVenda(null);
            venda.setSenha(null);
            venda.setStatus(Status.INATIVO);
        }
    }

    private void enviarAcessoConsumidor(Venda venda, String senha){
        if(venda.getStatusPedido().equals(StatusPedido.ENTREGUE)) {
            emailService.enviarEmailAcessoConsumidor(venda, senha);
        }
    }

    private void calcularValorVenda(Venda venda, RegistroVendaDTO dto, Loja loja){
        calcularValorVenda(venda, dto, loja, Collections.emptyMap());
    }

    private void calcularValorVenda(Venda venda, RegistroVendaDTO dto, Loja loja, Map<Long, Double> itensPreExistentes){
        venda.setPrecoTotal(BigDecimal.valueOf(0));
        List<Produto> produtos = produtoRepository
                .buscarProdutoParaVenda(dto.itensVenda().stream().map(ItemDTO::idProduto).toList(), loja.getIdLoja());

        adicionarItensNaVenda(venda, dto.itensVenda(), produtos, itensPreExistentes);

        venda.calcularPrecoTotal();
    }

    private void validarProdutoAtivo(Produto produto){
        if(produto.getStatus().equals(Status.INATIVO)){
            throw new RegraNegocioException(
                    String.format("Não é possível registrar uma venda com produto inativo: %d - %s",
                            produto.getIdProduto(), produto.getDescricao()));
        }
    }

    private void adicionarItensNaVenda(Venda venda, List<ItemDTO> itensDTO, List<Produto> produtos, Map<Long, Double> itensPreExistentes) {
        Map<Long, Produto> produtosPorId = produtos.stream()
                .collect(Collectors.toMap(Produto::getIdProduto, Function.identity()));

        for (ItemDTO i : itensDTO) {
            Produto produto = produtosPorId.get(i.idProduto());

            if (produto == null) {
                throw new ResourceNotFoundException("Produto não encontrado: " + i.idProduto());
            }

            validarDisponibilidadeProduto(produto, i.quantidade(), itensPreExistentes);

            ItemVenda item = new ItemVenda(produto.getPreco(), i.valorVendido(), i.quantidade(), i.detalhe(), venda, produto);
            venda.getItensVenda().add(item);

        }
    }

    private void validarDisponibilidadeProduto(Produto produto, double quantidade, Map<Long, Double> itensPreExistentes) {
        Double quantidadeAntiga = itensPreExistentes.get(produto.getIdProduto());

        if (quantidadeAntiga == null) {
            validarProdutoAtivo(produto);
        } else if (produto.getStatus().equals(Status.INATIVO) && quantidade > quantidadeAntiga) {
            throw new RegraNegocioException(
                    String.format("Não é possível aumentar a quantidade de um produto inativo. Estoque bloqueado: %d - %s",
                            produto.getIdProduto(), produto.getDescricao()));
        }
    }

    private Venda buscarVendaParaAtualizacao(Long idVenda, Long idLoja) {
        return repository.findByIdAndLojaIdLoja(idVenda, idLoja)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada."));
    }

    private void validarPermissaoEdicao(Venda venda) {
        if (venda.getStatusPedido() == StatusPedido.ENTREGUE || venda.getStatusPedido() == StatusPedido.CANCELADA) {
            throw new EdicaoNaoPermitidaException("Status atual da venda não permite edição de dados.");
        }
    }

    private void atualizarConsumidorSeNecessario(Venda venda, Long idConsumidor, Long idLoja) {
        if (!venda.getConsumidor().getIdConsumidor().equals(idConsumidor)) {
            Consumidor novoConsumidor = consumidorRepository.buscarConsumidorAtivo(idConsumidor, idLoja)
                    .orElseThrow(() -> new ResourceNotFoundException("Consumidor não localizado."));

            venda.setConsumidor(novoConsumidor);
        }
    }

    private void atualizarDadosDaVenda(Venda venda, RegistroVendaDTO dto) {
        venda.setDesconto(dto.desconto());
        venda.setPrazoTroca(dto.prazoTroca());
        venda.setPrazoDevolucao(dto.prazoDevolucao());
        venda.setStatusPedido(StatusPedido.converterDeStringParaEnum(dto.statusPedido()));
    }

    private Map<Long, Double> mapearItensPreExistentes(Venda venda) {
        return venda.getItensVenda().stream()
                .collect(Collectors.toMap(
                        item -> item.getProduto().getIdProduto(),
                        ItemVenda::getQuantidade
                ));
    }

    private void recalcularItensDaVenda(Venda venda, RegistroVendaDTO dto, Map<Long, Double> itensPreExistentes) {
        venda.getItensVenda().clear();
        calcularValorVenda(venda, dto, venda.getLoja(), itensPreExistentes);
    }
}
