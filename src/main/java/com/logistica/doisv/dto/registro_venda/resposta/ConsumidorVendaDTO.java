package com.logistica.doisv.dto.registro_venda.resposta;

import com.logistica.doisv.entities.Consumidor;

public record ConsumidorVendaDTO(Long id,
                                 String nome,
                                 String email,
                                 String celular,
                                 String endereco) {

    public ConsumidorVendaDTO(Consumidor consumidor){
        this(consumidor.getIdConsumidor(), consumidor.getNome(), consumidor.getEmail(),
                consumidor.getCelular(), consumidor.getEndereco());
    }
}
