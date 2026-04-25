package com.logistica.doisv.controllers;

import com.logistica.doisv.dto.AcessoDTO;
import com.logistica.doisv.dto.LojistaAtualizacaoDTO;
import com.logistica.doisv.dto.LojistaDTO;
import com.logistica.doisv.services.LojistaService;
import com.logistica.doisv.services.validacao.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("logvert/lojistas")
public class LojistaController {
    @Autowired
    private LojistaService lojistaService;

    @Autowired
    private TokenService tokenService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<LojistaDTO> buscarLojistaPorId(@PathVariable Long id, @AuthenticationPrincipal AcessoDTO usuarioLogado){

        return ResponseEntity.ok(lojistaService.buscarPorId(id, usuarioLogado.getIdLoja()));
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<LojistaDTO> buscarLojistaPorToken(@AuthenticationPrincipal AcessoDTO usuarioLogado){

        return ResponseEntity.ok(lojistaService.buscarPorId(usuarioLogado.getIdLojista(), usuarioLogado.getIdLoja()));
    }

    @GetMapping
    public ResponseEntity<List<LojistaDTO>> buscarTodosLojistasPorLoja(@AuthenticationPrincipal AcessoDTO usuarioLogado){

        return ResponseEntity.ok(lojistaService.buscarLojistasPorLoja(usuarioLogado.getIdLoja()));
    }

    @PostMapping
    public ResponseEntity<LojistaDTO> criarLojista(@Valid @RequestBody LojistaDTO dto,
                                                   @AuthenticationPrincipal AcessoDTO usuarioLogado){
        LojistaDTO lojistaCadastrado = lojistaService.salvar(dto, usuarioLogado.getIdLoja());

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(lojistaCadastrado.id())
                .toUri();

        return ResponseEntity.created(uri).body(lojistaCadastrado);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<LojistaDTO> atualizarLojista(@PathVariable Long id,
                                                       @Valid @RequestBody LojistaAtualizacaoDTO dto,
                                                       @AuthenticationPrincipal AcessoDTO usuarioLogado){
        LojistaDTO lojistaAtualizado = lojistaService.atualizar(id, dto, usuarioLogado.getIdLoja());

        return ResponseEntity.ok(lojistaAtualizado);
    }

    @PatchMapping
    public ResponseEntity<Void> desativarLojistas(@RequestBody List<Long> lojistasIds,
                                                  @AuthenticationPrincipal AcessoDTO usuarioLogado){
        lojistaService.inativar(lojistasIds, usuarioLogado.getIdLoja());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletarLojista(@PathVariable Long id, @AuthenticationPrincipal AcessoDTO usuarioLogado){
        lojistaService.remover(id, usuarioLogado.getIdLoja());
        return ResponseEntity.noContent().build();
    }
}
