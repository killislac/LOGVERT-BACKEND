package com.logistica.doisv.controllers.exceptions;

import com.logistica.doisv.dto.exceptions_dto.ErroCustomizado;
import com.logistica.doisv.dto.exceptions_dto.ErroValidacao;
import com.logistica.doisv.services.exceptions.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;

@ControllerAdvice
public class ControllerException {

    //Campos Nulos, Campos faltando, Sem Relacionamento -> 422  
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> argumentoInvalido(MethodArgumentNotValidException e, HttpServletRequest requisicao) {
        ErroValidacao erros = new ErroValidacao(Instant.now(), HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Dados inválidos", requisicao.getRequestURI());

        e.getBindingResult().getFieldErrors().forEach(erro -> {
            String campo = erro.getField();
            String mensagem = erro.getDefaultMessage();
            erros.adicionarErro(campo, mensagem);
        });

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erros);
    }

    //Recurso não encontrado -> 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> recursoNaoEncontrado(ResourceNotFoundException e, HttpServletRequest requisicao) {
        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.NOT_FOUND.value(),
                e.getMessage(), requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    //Valores incorretos(permissoes), tentar cadastrar item único mais de uma vez, relacionamento inexistente -> 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> violacaoDeIntegridade(DataIntegrityViolationException e, HttpServletRequest requisicao) {
        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.CONFLICT.value(),
                e.getMessage(), requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    //Requisição sem body -> 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> requisicaoSemConteudo(HttpMessageNotReadableException e, HttpServletRequest requisicao) {
        String mensagem = e.getMessage().contains("Required request body is missing") ?
                "Corpo da requisição não pode estar vazio." : "Corpo da requisição inválido. Verifique o formato JSON.";

        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.BAD_REQUEST.value(),
                mensagem, requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErroCustomizado> tipoMidiaNaoSuportado(HttpMediaTypeNotSupportedException e,
                                                                 HttpServletRequest requisicao) {
        String mensagem = String.format(
                "Tipo de mídia '%s' não suportado. Utilize application/json", e.getContentType());

        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                mensagem, requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(erro);
    }

    //Requisição sem os parametros esperados -> 400
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<?> requisicaoSemParametro(MissingServletRequestPartException e,
                                                    HttpServletRequest requisicao) {
        String mensagem = String.format("A parte '%s' da requisição não está presente.", e.getRequestPartName());

        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.BAD_REQUEST.value(),
                mensagem, requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    //Tentativa de acessar um recurso que pertence a outro registro -> 403
    @ExceptionHandler(AssociacaoInvalidaException.class)
    public ResponseEntity<?> associacaoNaoPermitida(AssociacaoInvalidaException e, HttpServletRequest requisicao){
        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.FORBIDDEN.value(),
                e.getMessage(), requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    //Erro de permissão para acessar o Google Drive ao anexar -> 403
    @ExceptionHandler(GeneralSecurityException.class)
    public ResponseEntity<?> erroPermissaoGoogleDrive(GeneralSecurityException e, HttpServletRequest requisicao){
        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.FORBIDDEN.value(),
                e.getMessage(), requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    //Erro no formato do arquivo enviado ao Google Drive -> 400
    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> erroFormatoAnexoGoogleDrive(IOException e, HttpServletRequest requisicao){
        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.BAD_REQUEST.value(),
                e.getMessage(), requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    //Erro ao editar com status que não permite edição
    @ExceptionHandler(EdicaoNaoPermitidaException.class)
    public ResponseEntity<?> edicaoNaoPermitida (EdicaoNaoPermitidaException e, HttpServletRequest requisicao){
        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.CONFLICT.value(),
                e.getMessage(), requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<?> erroAoEnviarEmail (MessagingException e, HttpServletRequest requisicao){
        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage(), requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<?> erroNaRegraDeNegocio (RegraNegocioException e, HttpServletRequest requisicao){
        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.CONFLICT.value(),
                e.getMessage(), requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> erroArgumento (IllegalArgumentException e, HttpServletRequest requisicao){
        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.CONFLICT.value(),
                e.getMessage(), requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> erroParteRequisicaoFaltando (IllegalStateException e, HttpServletRequest requisicao){
        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.CONFLICT.value(),
                e.getMessage(), requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> erroDeTipoInvalido(MethodArgumentTypeMismatchException e, HttpServletRequest requisicao){
        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.CONFLICT.value(),
                e.getMessage(), requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(UsuarioInativoException.class)
    public ResponseEntity<?> erroDeUsuarioInativo(UsuarioInativoException e, HttpServletRequest requisicao){
        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.UNAUTHORIZED.value(),
                e.getMessage(), requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(CodigoExpiradoException.class)
    public ResponseEntity<?> erroCodigoRecuperacaoExpirado(CodigoExpiradoException e, HttpServletRequest requisicao){
        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.GONE.value(),
                e.getMessage(), requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.GONE).body(erro);
    }

    @ExceptionHandler(SenhaFracaException.class)
    public ResponseEntity<?> erroSenhaFraca(SenhaFracaException e, HttpServletRequest requisicao){
        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.BAD_REQUEST.value(),
                e.getMessage(), requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(TipoArquivoInvalidoException.class)
    public ResponseEntity<?> erroTipoDeArquivoInvalido(TipoArquivoInvalidoException e, HttpServletRequest requisicao){
        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.BAD_REQUEST.value(),
                e.getMessage(), requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroCustomizado> handleGenericException(Exception e, HttpServletRequest requisicao) {
        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage(), requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

    @ExceptionHandler(EntidadeDuplicadaException.class)
    public ResponseEntity<ErroCustomizado> entidadeDuplicada(EntidadeDuplicadaException e,
                                               HttpServletRequest requisicao) {

        ErroCustomizado erro = new ErroCustomizado(Instant.now(), HttpStatus.CONFLICT.value(),
                e.getMessage(), requisicao.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }
}
