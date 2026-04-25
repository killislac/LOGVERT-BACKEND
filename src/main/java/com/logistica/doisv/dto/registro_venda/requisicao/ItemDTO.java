package com.logistica.doisv.dto.registro_venda.requisicao;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ItemDTO(@NotNull(message = "O ID do produto é obrigatório.") Long idProduto,
                      @Positive(message = "A quantidade deve ser maior que zero.") Double quantidade,
                      String detalhe,
                      @DecimalMin(value = "0.0", message = "O valor vendido deve ser maior ou igual a 0,00.")
                      @Digits(integer = 6, fraction = 2, message = "O valor vendido deve ter no máximo 6 dígitos inteiros e 2 casas decimais.")
                      BigDecimal valorVendido) {
}
