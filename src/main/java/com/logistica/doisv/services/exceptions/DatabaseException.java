package com.logistica.doisv.services.exceptions;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String mensagem) {
        super(mensagem);
    }
}
