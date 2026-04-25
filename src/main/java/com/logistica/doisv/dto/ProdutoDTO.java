package com.logistica.doisv.dto;

import com.logistica.doisv.entities.Produto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.math.BigDecimal;
@Builder
public record ProdutoDTO(Long idProduto,
                         @NotBlank(message = "A descrição do produto é obrigatória.") String descricao,
                         @NotBlank(message = "A unidade de medida é obrigatória.") String unidadeMedida,
                         @DecimalMin(value = "0.0", message = "O preço deve ser maior ou igual a 0,00.")
                         @Digits(integer = 6, fraction = 2, message = "O preço deve ter no máximo 6 dígitos inteiros e 2 casas decimais.")
                         BigDecimal preco,
                         String imagem,
                         String status) {

    public ProdutoDTO(Produto produto){
        this(produto.getIdProduto(), produto.getDescricao(), produto.getUnidadeMedida(), produto.getPreco(),
                produto.getImagem(), produto.getStatus().toString());
    }
}
