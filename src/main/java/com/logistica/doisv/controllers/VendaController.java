package com.logistica.doisv.controllers;

import com.logistica.doisv.dto.AcessoDTO;
import com.logistica.doisv.dto.registro_venda.requisicao.RegistroVendaDTO;
import com.logistica.doisv.dto.registro_venda.resposta.VendaDTO;
import com.logistica.doisv.dto.registro_venda.resposta.VendaResumidaDTO;
import com.logistica.doisv.services.VendaService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("logvert/vendas")
public class VendaController {

    @Autowired
    private VendaService service;

    @GetMapping
    public ResponseEntity<Page<VendaResumidaDTO>> buscarTodasVendas(Pageable pageable, @AuthenticationPrincipal AcessoDTO usuarioLogado){
        Page<VendaResumidaDTO> dadosVendas = service.buscarTodasVendasPorLoja(pageable, usuarioLogado.getIdLoja());
        return ResponseEntity.ok(dadosVendas);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<VendaDTO> buscarVendaPorId(@PathVariable Long id, @AuthenticationPrincipal AcessoDTO usuarioLogado){
        VendaDTO dto = service.buscarPorId(id, usuarioLogado.getIdLoja());
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/me")
    public ResponseEntity<VendaDTO> buscarVendaPorToken(@AuthenticationPrincipal AcessoDTO usuarioLogado){

        return ResponseEntity.ok(service.buscarPorId(usuarioLogado.getIdVenda(), usuarioLogado.getIdLoja()));
    }

    @PostMapping
    public ResponseEntity<VendaDTO> criarVenda(@Valid @RequestBody RegistroVendaDTO dto,
                                               @AuthenticationPrincipal AcessoDTO usuarioLogado) throws MessagingException {
        VendaDTO venda = service.salvar(dto, usuarioLogado.getIdLoja());
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(venda.idVenda()).toUri();
        return ResponseEntity.created(uri).body(venda);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<VendaDTO> atualizarVenda(@PathVariable Long id, @Valid @RequestBody RegistroVendaDTO dto,
                                                   @AuthenticationPrincipal AcessoDTO usuarioLogado) throws MessagingException {
        VendaDTO venda = service.atualizar(id, dto, usuarioLogado.getIdLoja());
        return ResponseEntity.ok(venda);
    }

    @PatchMapping
    public ResponseEntity<Void> desativarVenda(@RequestBody List<Long> vendasIds,
                                               @AuthenticationPrincipal AcessoDTO usuarioLogado){
        service.inativar(vendasIds, usuarioLogado.getIdLoja());
        return ResponseEntity.noContent().build();
    }
}
