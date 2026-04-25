package com.logistica.doisv.controllers;

import com.logistica.doisv.dto.AcessoDTO;
import com.logistica.doisv.dto.ConsumidorDTO;
import com.logistica.doisv.services.ConsumidorService;
import com.logistica.doisv.services.validacao.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("logvert/consumidores")
public class ConsumidorController {
    @Autowired
    private ConsumidorService consumidorService;

    @Autowired
    private TokenService tokenService;

    @GetMapping
    public ResponseEntity<List<ConsumidorDTO>> buscarTodosConsumidores(@AuthenticationPrincipal AcessoDTO usuarioLogado) {

        return ResponseEntity.ok(consumidorService.buscarTodos(usuarioLogado.getIdLoja()));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ConsumidorDTO> buscarConsumidorPorId(@PathVariable Long id,
                                                               @AuthenticationPrincipal AcessoDTO usuarioLogado) {
        return ResponseEntity.ok(consumidorService.buscarPorId(id, usuarioLogado.getIdLoja()));
    }

    @PostMapping
    public ResponseEntity<ConsumidorDTO> criarConsumidor(@Valid @RequestBody ConsumidorDTO dto,
                                                         @AuthenticationPrincipal AcessoDTO usuarioLogado) {
        dto = consumidorService.salvar(dto, usuarioLogado.getIdLoja());
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.idConsumidor()).toUri();

        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ConsumidorDTO> atualizarConsumidor(@PathVariable Long id,
                                                             @Valid @RequestBody ConsumidorDTO dto,
                                                             @AuthenticationPrincipal AcessoDTO usuarioLogado) {

        return ResponseEntity.ok().body(consumidorService.atualizar(dto, id, usuarioLogado.getIdLoja()));
    }

    @PatchMapping
    public ResponseEntity<Void> desativarConsumidor(@RequestBody List<Long> consumidoresIds,
                                                    @AuthenticationPrincipal AcessoDTO usuarioLogado){
        consumidorService.inativar(consumidoresIds, usuarioLogado.getIdLoja());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletarConsumidor(@PathVariable Long id,
                                                  @AuthenticationPrincipal AcessoDTO usuarioLogado) {
        consumidorService.remover(id, usuarioLogado.getIdLoja());
        return ResponseEntity.noContent().build();
    }
}
