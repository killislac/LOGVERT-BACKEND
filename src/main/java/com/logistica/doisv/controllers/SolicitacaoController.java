package com.logistica.doisv.controllers;

import com.logistica.doisv.dto.AcessoDTO;
import com.logistica.doisv.dto.registro_solicitacao.CriarSolicitacaoDTO;
import com.logistica.doisv.dto.registro_solicitacao.HistoricoSolicitacaoDTO;
import com.logistica.doisv.dto.registro_solicitacao.SolicitacaoDetalhadaDTO;
import com.logistica.doisv.dto.registro_solicitacao.SolicitacaoResumidaDTO;
import com.logistica.doisv.dto.registro_venda.requisicao.ItemDTO;
import com.logistica.doisv.services.SolicitacaoService;
import com.logistica.doisv.services.validacao.TokenService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("logvert/solicitacoes")
public class SolicitacaoController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SolicitacaoService service;

    @GetMapping
    public ResponseEntity<Page<SolicitacaoResumidaDTO>> buscarTodasSolicitacoes(Pageable pageable, @AuthenticationPrincipal AcessoDTO usuarioLogado){

        return ResponseEntity.ok(service.buscarTodos(pageable, usuarioLogado.getIdLoja()));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<SolicitacaoDetalhadaDTO> buscarSolicitacaoPorId(@PathVariable Long id, @AuthenticationPrincipal AcessoDTO usuarioLogado){

        return ResponseEntity.ok(service.buscarPorId(id, usuarioLogado.getIdLoja()));
    }

    @PostMapping(value = "/criar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SolicitacaoResumidaDTO> criarSolicitacao(@Valid @RequestPart("solicitacao") CriarSolicitacaoDTO dto,
                                                                   @RequestPart("anexos") List<MultipartFile> anexos,
                                                                   @AuthenticationPrincipal AcessoDTO usuarioLogado) throws GeneralSecurityException, IOException {

        return ResponseEntity.ok(service.registrarSolicitacao(dto, anexos, usuarioLogado.getIdVenda()));
    }

    @PostMapping(value = "/atualizar/{id}")
    public ResponseEntity<SolicitacaoDetalhadaDTO> atualizarSolicitacao(@PathVariable Long id,
                                                  @Valid @RequestPart("historico") HistoricoSolicitacaoDTO dto,
                                                  @RequestPart(value = "novosProdutos", required = false) List<ItemDTO> novosProdutos,
                                                  @AuthenticationPrincipal AcessoDTO usuarioLogado) throws MessagingException {

        return ResponseEntity.ok(service.atualizarSolicitacao(id, dto, usuarioLogado.getIdLoja(), novosProdutos));
    }

    @PutMapping(value = "/aprovar/{id}")
    public ResponseEntity<SolicitacaoResumidaDTO> aprovarSolicitacao(@PathVariable Long id, @AuthenticationPrincipal AcessoDTO usuarioLogado){

        return ResponseEntity.ok(service.aprovarSolicitacao(id, usuarioLogado.getIdLoja()));
    }

    @PutMapping(value = "/reprovar/{id}")
    public ResponseEntity<SolicitacaoResumidaDTO> reprovarSolicitacao(@PathVariable Long id, @AuthenticationPrincipal AcessoDTO usuarioLogado) throws GeneralSecurityException, IOException {

        return ResponseEntity.ok(service.reprovarSolicitacao(id, usuarioLogado.getIdLoja()));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<SolicitacaoResumidaDTO> editarSolicitacao(@PathVariable Long id,
                                                                    @Valid @RequestPart("solicitacao") CriarSolicitacaoDTO dto,
                                                                    @RequestPart("anexos") List<MultipartFile> anexos,
                                                                    @AuthenticationPrincipal AcessoDTO usuarioLogado) throws GeneralSecurityException, IOException {

        return ResponseEntity.ok(service.editarSolicitacao(id, dto, anexos, usuarioLogado.getIdLoja()));
    }

    @PutMapping(value = "/cancelar/{id}")
    public ResponseEntity<SolicitacaoResumidaDTO> cancelarSolicitacao(@PathVariable Long id,
                                                                      @Valid @RequestBody CriarSolicitacaoDTO dto,
                                                                      @AuthenticationPrincipal AcessoDTO usuarioLogado) throws GeneralSecurityException, IOException {

        return ResponseEntity.ok(service.cancelarSolicitacao(id, dto, usuarioLogado.getIdLoja()));
    }
}
