package com.logistica.doisv.services;

import com.logistica.doisv.dto.ProdutoDTO;
import com.logistica.doisv.entities.Loja;
import com.logistica.doisv.entities.Produto;
import com.logistica.doisv.entities.enums.CategoriaArquivoPermitida;
import com.logistica.doisv.entities.enums.Status;
import com.logistica.doisv.repositories.LojaRepository;
import com.logistica.doisv.repositories.ProdutoRepository;
import com.logistica.doisv.services.api.GoogleDriveService;
import com.logistica.doisv.services.exceptions.*;
import com.logistica.doisv.util.validacao.ArquivoValidador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ProdutoService {
    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private LojaRepository lojaRepository;

    @Autowired
    private ArquivoValidador arquivoValidador;

    @Transactional(readOnly = true)
    public Page<ProdutoDTO> buscarTodos(Pageable pageable, Long idLoja){
        Page<Produto> produtos = repository.findAllByLojaComFetch(idLoja, pageable);
        return produtos.map(ProdutoDTO::new);
    }

    @Transactional(readOnly = true)
    public ProdutoDTO buscarPorId(Long id, Long idLoja){
        Produto produto = repository.findByIdProdutoAndLojaIdLoja(id, idLoja)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        return new ProdutoDTO(produto);
    }

    @Transactional
    public ProdutoDTO salvar(ProdutoDTO dto, MultipartFile imagem, Long idLoja) throws IOException, GeneralSecurityException{
        Produto produto = new Produto();

        arquivoValidador.validarOpcional(imagem, Set.of(CategoriaArquivoPermitida.IMAGEM));

        dtoParaEntidade(dto, produto);
        produto.setLoja(lojaRepository.findById(idLoja).orElseThrow(() -> new ResourceNotFoundException("Loja não encontrada")));

        validarProdutoUnico(produto, null);

        if(!imagem.isEmpty()){
            repository.save(produto);
            String url = GoogleDriveService.salvarArquivoDrive(imagem, produto.getIdProduto().toString(), produto.getClass().getSimpleName());
            produto.setImagem(url.split("/")[5]);
        }

        return new ProdutoDTO(repository.save(produto));
    }

    @Transactional
    public ProdutoDTO atualizar(Long idProduto, ProdutoDTO dto, Long idLoja, MultipartFile imagem) throws IOException, GeneralSecurityException{
        Produto produto = repository.findByIdProdutoAndLojaIdLoja(idProduto, idLoja)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        arquivoValidador.validarOpcional(imagem, Set.of(CategoriaArquivoPermitida.IMAGEM));

        dtoParaEntidade(dto, produto);

        validarProdutoUnico(produto, produto.getIdProduto());

        if(imagem.getContentType() != null) {
            String url = GoogleDriveService.salvarArquivoDrive(imagem, produto.getIdProduto().toString(), produto.getClass().getSimpleName());
            produto.setImagem(url.split("/")[5]);
        }

        produto = repository.save(produto);

        return new ProdutoDTO(produto);
    }

    @Transactional
    public void remover(Long idProduto, Long idLoja){
        try {
            Produto produto = repository.findByIdProdutoAndLojaIdLoja(idProduto, idLoja)
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

            excluirImagemProduto(produto);

            repository.delete(produto);
        }catch(DataIntegrityViolationException e){
            throw new DatabaseException("Falha na integridade referencial");
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void inativar(List<Long> ids, Long idLoja){
        int produtosInativados = repository.atualizarStatusProdutos(ids, idLoja, Status.INATIVO);

        if (produtosInativados <= 0) {
            throw new RegraNegocioException("Nenhum produto encontrado para inativação com os IDs informados para esta loja.");
        }
    }

    @Transactional
    public List<ProdutoDTO> importarProdutos(MultipartFile produtosCsv, Long idLoja){
        arquivoValidador.validarObrigatorio(produtosCsv, Set.of(CategoriaArquivoPermitida.CSV));

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(produtosCsv.getInputStream()))){
            String linha = reader.readLine();
            arquivoValidador.validarEstruturaCsv(linha);

            List<Produto> produtos = new ArrayList<>();

            while (linha != null){
                linha = reader.readLine();

                if(linha != null){
                    arquivoValidador.validarEstruturaCsv(linha);
                    produtos.add(converterLinhaParaProduto(linha, idLoja));
                }
            }

            return repository.saveAll(produtos)
                    .stream()
                    .map(ProdutoDTO::new)
                    .toList();

        }catch (IOException e){
            throw new TipoArquivoInvalidoException("Erro ao validar estrutura do CSV.");
        }
    }


    private void dtoParaEntidade(ProdutoDTO dto, Produto produto){
        produto.setDescricao(dto.descricao());
        produto.setUnidadeMedida(dto.unidadeMedida());
        produto.setPreco(dto.preco());
        if (dto.status() != null && !dto.status().isBlank()){
            produto.setStatus(Status.converterStringParaEnum(dto.status()));
        }
    }

    private Produto converterLinhaParaProduto(String linha, Long idLoja){
        String[] campos = linha.split(";");

        if (campos.length < 3) {
            throw new RegraNegocioException("Linha do CSV com formato inválido: " + linha);
        }

        try {
            String descricao = campos[0].trim();
            String unidadeMedida = campos[1].trim();

            String precoTexto = campos[2].replace(",",".");
            BigDecimal preco = new BigDecimal(precoTexto);

            if(preco.compareTo(BigDecimal.ZERO) < 0){
                throw new RegraNegocioException("O valor não poder ser negativo: " + linha);
            }

            Produto produto = new Produto();
            produto.setDescricao(descricao);
            produto.setUnidadeMedida(unidadeMedida);
            produto.setPreco(preco);

            Loja loja = new Loja();
            loja.setIdLoja(idLoja);
            produto.setLoja(loja);

            validarProdutoUnico(produto, null);

            return produto;
        }catch (NumberFormatException e) {
            throw new RegraNegocioException("Preço inválido na linha: " + linha);
        }
    }

    private void excluirImagemProduto(Produto produto) throws GeneralSecurityException, IOException {
        if (produto.getImagem() == null || produto.getImagem().isBlank()) {
            return;
        }

        GoogleDriveService.excluirArquivoDrive(produto.getIdProduto().toString(), produto.getClass().getSimpleName());
    }

    private void validarProdutoUnico(Produto produto, Long idIgnorar){
        boolean existeProduto;

        if(idIgnorar == null){
            existeProduto = repository
                    .existsByDescricaoAndLojaIdLoja(produto.getDescricao(), produto.getLoja().getIdLoja());
        }else{
            existeProduto = repository
                    .existsByDescricaoAndLojaIdLojaAndIdProdutoNot(produto.getDescricao(),
                            produto.getLoja().getIdLoja(), idIgnorar);
        }

        if(existeProduto){
            throw new EntidadeDuplicadaException(String
                    .format("Já existe produto com descrição '%s' cadastrado no sistema", produto.getDescricao()));
        }
    }
}
