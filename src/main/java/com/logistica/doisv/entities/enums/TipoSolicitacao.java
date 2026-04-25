package com.logistica.doisv.entities.enums;

import lombok.Getter;

public enum TipoSolicitacao {
    TROCA("Troca"),
    DEVOLUCAO("Devolução");

    @Getter
    private final String descricao;

    TipoSolicitacao(String tipo){
        this.descricao = tipo;
    }

    public static TipoSolicitacao deString(String tipoSolicitacao){
        for(TipoSolicitacao t : TipoSolicitacao.values()){
            if(t.name().equalsIgnoreCase(tipoSolicitacao) || t.descricao.equalsIgnoreCase(tipoSolicitacao)){
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de Solicitação inválido: " + tipoSolicitacao);
    }
}
