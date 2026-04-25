package com.logistica.doisv.dto.registro_solicitacao;

import com.logistica.doisv.entities.ItemVenda;

import java.math.BigDecimal;

public record ProdutoSolicitacaoDTO(Long idItemVenda, Long idProduto, String descricao,
                                    String unidadeMedida, BigDecimal precoVendido, String imagem) {

    public ProdutoSolicitacaoDTO(ItemVenda itemVenda){
        this(itemVenda.getId(), itemVenda.getProduto().getIdProduto(), itemVenda.getProduto().getDescricao(),
                itemVenda.getProduto().getUnidadeMedida(), itemVenda.getPrecoVendido(),
                itemVenda.getProduto().getImagem());
    }
}
