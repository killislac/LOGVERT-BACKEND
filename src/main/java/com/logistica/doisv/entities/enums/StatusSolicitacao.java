package com.logistica.doisv.entities.enums;

import lombok.Getter;

public enum StatusSolicitacao {
    PENDENTE("Pendente"),
    APROVADA("Aprovada"),
    REJEITADA("Rejeitada"),
    CANCELADA("Cancelada"),
    EM_ANDAMENTO("Em andamento"),
    EM_TRANSITO("Em trânsito"),
    CONCLUIDA("Concluída");

    @Getter
    private final String statusSolicitacao;

    StatusSolicitacao(String status){this.statusSolicitacao = status;}

    public static StatusSolicitacao deString(String status){
        for(StatusSolicitacao s : StatusSolicitacao.values()){
            if(s.statusSolicitacao.equalsIgnoreCase(status)){
                return s;
            }
        }
        throw new IllegalArgumentException("Status de Solicitação não localizado");
    }
}
