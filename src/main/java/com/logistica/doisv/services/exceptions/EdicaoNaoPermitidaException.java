package com.logistica.doisv.services.exceptions;

public class EdicaoNaoPermitidaException extends RuntimeException{
    public EdicaoNaoPermitidaException(String mensagem){
        super(mensagem);
    }
}
