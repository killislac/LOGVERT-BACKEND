package com.logistica.doisv.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.logistica.doisv.dto.ConsumidorLoginDTO;
import com.logistica.doisv.dto.LoginDTO;
import com.logistica.doisv.dto.LoginResponse;
import com.logistica.doisv.services.AutenticacaoService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logvert/login")
public class LoginController {

    @Autowired
    private AutenticacaoService autenticacaoService ;

    @PostMapping
    public ResponseEntity<?> loginLojista(@Valid @RequestBody LoginDTO dto) {
        String token = autenticacaoService.loginLojista(dto.email(), dto.password());
        if (token != null) {
            return ResponseEntity.ok(new LoginResponse(token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos");
    }

    @PostMapping(value = "/consumidores")
    public ResponseEntity<?> loginConsumidor(@Valid @RequestBody ConsumidorLoginDTO dto){
        String token = autenticacaoService.loginConsumidor(dto);
        if(token != null){
            return ResponseEntity.ok(new LoginResponse(token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Serial ou senha inválidos");
    }

    @PostMapping(value = "/lojista/recuperar-senha")
    public ResponseEntity<String> recuperarSenha(@RequestBody JsonNode body) throws MessagingException {
        String email = body.path("email").asText(null);
        autenticacaoService.recuperarSenhaLojista(email);

        return ResponseEntity.ok(String.format("Código de recuperação enviado para o email %s", email));
    }

    @PostMapping(value = "/lojista/validar-recuperacao")
    public ResponseEntity<Void> validarCodigoRecuperacao(@RequestBody JsonNode body){
        String email = body.path("email").asText(null);
        String codigoRecuperacao = body.path("codigoRecuperacao").asText(null);

        autenticacaoService.validarCodigoRecuperacao(email, codigoRecuperacao);

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/lojista/atualizar-senha")
    public ResponseEntity<String> atualizarSenha(@RequestBody JsonNode body){
        String email = body.path("email").asText(null);
        String codigoRecuperacao = body.path("codigoRecuperacao").asText(null);
        String novaSenha = body.path("novaSenha").asText(null);

        autenticacaoService.atualizarSenha(email, codigoRecuperacao, novaSenha);

        return ResponseEntity.ok("Senha atualizada com sucesso.");
    }
}
