package com.logistica.doisv.dto;

import com.logistica.doisv.util.validacao.CpfCnpj;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LojistaAtualizacaoDTO(@NotBlank(message = "O nome do lojista é obrigatório.") String nome,
                                    @CpfCnpj(message = "Informe um CPF válido.")
                                    @NotNull(message = "O CPF é obrigatório.") String cpf,
                                    @NotBlank(message = "O e-mail é obrigatório.")
                                    @Email(message = "Informe um e-mail válido.") String email,
                                    String status) {
}
