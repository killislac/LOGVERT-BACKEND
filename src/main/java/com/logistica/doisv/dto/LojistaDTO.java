package com.logistica.doisv.dto;

import com.logistica.doisv.entities.Lojista;
import com.logistica.doisv.util.validacao.CpfCnpj;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LojistaDTO(Long id,
                         @NotBlank(message = "O nome do lojista é obrigatório.") String nome,
                         @CpfCnpj(message = "Informe um CPF válido.")
                         @NotNull(message = "O CPF é obrigatório.") String cpf,
                         @NotBlank(message = "O e-mail é obrigatório.")
                         @Email(message = "Informe um e-mail válido.") String email,
                         @NotBlank(message = "A senha é obrigatória.") String password,
                         Long idLoja,
                         String status) {

    public LojistaDTO {
        cpf = cpf != null ? cpf.replaceAll("[^0-9A-Za-z]", "").toUpperCase() : null;
    }

        public LojistaDTO(Lojista lojista){
            this(lojista.getIdLojista(), lojista.getNome(), lojista.getCpf(), lojista.getEmail(), "", lojista.getLoja().getIdLoja(),
                        lojista.getStatus().toString());
        }
}

