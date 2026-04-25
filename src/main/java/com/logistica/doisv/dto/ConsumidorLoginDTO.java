package com.logistica.doisv.dto;

import jakarta.validation.constraints.NotBlank;

public record ConsumidorLoginDTO(@NotBlank(message = "O serial é obrigatório.") String serial,
								 @NotBlank(message = "A senha é obrigatória.") String senha) {
}
