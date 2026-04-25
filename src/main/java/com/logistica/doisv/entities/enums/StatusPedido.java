package com.logistica.doisv.entities.enums;

import lombok.Getter;

public enum StatusPedido {
    ENTREGUE("Entregue"),
    EM_ANDAMENTO("Em Andamento"),
    CANCELADA("Cancelada");

    @Getter
    private final String statusPedido;

    StatusPedido(String status){
        this.statusPedido = status;
    }

    public static StatusPedido converterDeStringParaEnum(String status){
        for(StatusPedido pedido : StatusPedido.values()){
            if (pedido.statusPedido.equalsIgnoreCase(status))
                return pedido;
        }
        throw new IllegalArgumentException("Status não localizado");
    }
}
