package com.logistica.doisv.dto.registro_venda.resposta;

import com.logistica.doisv.entities.Produto;

public record ProdutoVendaDTO(Long id, String descricao) {
    public ProdutoVendaDTO(Produto produto){
        this(produto.getIdProduto(), produto.getDescricao());
    }
}
