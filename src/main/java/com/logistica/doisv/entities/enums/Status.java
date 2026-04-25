package com.logistica.doisv.entities.enums;

import lombok.Getter;

public enum Status {
    ATIVO("Ativo"),
    INATIVO("Inativo");

    @Getter
    private String statusItem;

    Status(String statusItem){
        this.statusItem = statusItem;
    }

    public static Status converterStringParaEnum(String statusString){
        for(Status status : Status.values()){
            if(status.statusItem.equalsIgnoreCase(statusString))
                return status;
        }
        throw new IllegalArgumentException("Status não localizado");
    }
}
