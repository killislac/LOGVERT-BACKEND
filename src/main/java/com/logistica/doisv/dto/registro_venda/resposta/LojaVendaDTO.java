package com.logistica.doisv.dto.registro_venda.resposta;

import com.logistica.doisv.entities.Loja;

public record LojaVendaDTO(Long id,
                           String nome,
                           String email) {

    public LojaVendaDTO(Loja loja){
        this(loja.getIdLoja(), loja.getNome(), loja.getEmail());
    }
}
