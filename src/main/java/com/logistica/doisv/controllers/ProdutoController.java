package com.logistica.doisv.controllers;

import com.logistica.doisv.dto.AcessoDTO;
import com.logistica.doisv.dto.ProdutoDTO;
import com.logistica.doisv.services.ProdutoService;
import com.logistica.doisv.services.validacao.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("logvert/produtos")
public class ProdutoController {
    @Autowired
    private ProdutoService service;

    @Autowired
    private TokenService tokenService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProdutoDTO> buscarProdutoPorId(@PathVariable Long id,
                                                         @AuthenticationPrincipal AcessoDTO usuarioLogado){
        ProdutoDTO dto = service.buscarPorId(id, usuarioLogado.getIdLoja());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoDTO>> buscarTodosProdutos(Pageable pageable,
                                                                @AuthenticationPrincipal AcessoDTO usuarioLogado) {
        Page<ProdutoDTO> dto = service.buscarTodos(pageable, usuarioLogado.getIdLoja());
        return ResponseEntity.ok(dto);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProdutoDTO> criarProduto(@Valid @RequestPart("produto") ProdutoDTO dto,
                                                   @RequestPart("imagem") MultipartFile imagem,
                                                   @AuthenticationPrincipal AcessoDTO usuarioLogado) throws GeneralSecurityException, IOException {

        dto = service.salvar(dto, imagem, usuarioLogado.getIdLoja());
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.idProduto()).toUri();

        return ResponseEntity.created(uri).body(dto);
    }

    @PostMapping(value = "/importar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ProdutoDTO>> importarProdutos(@RequestPart(value = "produtosCsv") MultipartFile produtosCsv,
                                                             @AuthenticationPrincipal AcessoDTO usuarioLogado){

        return ResponseEntity.status(HttpStatus.CREATED).body(service.importarProdutos(produtosCsv, usuarioLogado.getIdLoja()));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProdutoDTO> atualizarProduto(@PathVariable Long id,
                                                       @Valid @RequestPart("produto") ProdutoDTO dto,
                                                       @RequestPart("imagem") MultipartFile imagem,
                                                       @AuthenticationPrincipal AcessoDTO usuarioLogado) throws GeneralSecurityException, IOException {
        dto = service.atualizar(id, dto, usuarioLogado.getIdLoja(), imagem);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping
    public ResponseEntity<Void> desativarProduto(@RequestBody List<Long> produtosIds,
                                                 @AuthenticationPrincipal AcessoDTO usuarioLogado){
        service.inativar(produtosIds, usuarioLogado.getIdLoja());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id, @AuthenticationPrincipal AcessoDTO usuarioLogado) {
        service.remover(id, usuarioLogado.getIdLoja());

        return ResponseEntity.noContent().build();
    }
}
