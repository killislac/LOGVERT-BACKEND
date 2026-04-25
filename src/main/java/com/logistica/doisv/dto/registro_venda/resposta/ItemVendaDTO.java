package com.logistica.doisv.dto.registro_venda.resposta;

import com.logistica.doisv.entities.ItemVenda;

import java.math.BigDecimal;

public record ItemVendaDTO(Long id,
                           BigDecimal precoOriginal,
                           BigDecimal precoVendido,
                           BigDecimal percentualVariacao,
                           Double quantidade,
                           String detalhes,
                           ProdutoVendaDTO produto) {

    public ItemVendaDTO(ItemVenda item){
        this(item.getId(), item.getPrecoOriginal(), item.getPrecoVendido(), item.getPercentualVariacao(), item.getQuantidade(),
                item.getDetalhes(), new ProdutoVendaDTO(item.getProduto()));
    }

}
