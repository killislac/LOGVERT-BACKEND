package com.logistica.doisv.dto.registro_solicitacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CriarSolicitacaoDTO(@NotNull(message = "O ID do item é obrigatório.") Long idItem,
                                  @Positive(message = "A quantidade deve ser maior que zero.") Double quantidade,
                                  @NotBlank(message = "O tipo da solicitação é obrigatório.") String tipo,
                                  @NotBlank(message = "O motivo da solicitação é obrigatório.") String motivo) {
}
