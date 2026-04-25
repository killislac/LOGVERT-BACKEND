package com.logistica.doisv.entities.enums;

import lombok.Getter;

public enum TipoFeedback {
    LOJA("Loja"),
    SOLICITACAO("Solicitação");

    @Getter
    private final String descricao;

    TipoFeedback(String tipo){
        this.descricao = tipo;
    }

    public static TipoFeedback deString(String tipoFeedback){
        for(TipoFeedback f : TipoFeedback.values()){
            if(f.name().equalsIgnoreCase(tipoFeedback) || f.descricao.equalsIgnoreCase(tipoFeedback)){
                return f;
            }
        }
        throw new IllegalArgumentException("Tipo de Feedback inválido: " + tipoFeedback);
    }
}
