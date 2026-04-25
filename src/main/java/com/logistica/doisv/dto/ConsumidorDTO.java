package com.logistica.doisv.dto;

import com.logistica.doisv.entities.Consumidor;
import com.logistica.doisv.util.validacao.CpfCnpj;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ConsumidorDTO(Long idConsumidor,
                            @NotBlank(message = "O nome do consumidor é obrigatório.") String nome,
                            @CpfCnpj(message = "Informe um CPF/CNPJ válido.") String cpf_cnpj,
                            @Email(message = "Informe um e-mail válido.") String email,
                            @NotBlank(message = "O celular é obrigatório.") String celular,
                            String telefone,
                            String endereco,
                            String status) {

    public ConsumidorDTO {
        cpf_cnpj = cpf_cnpj != null ? cpf_cnpj.replaceAll("[^0-9A-Za-z]", "").toUpperCase() : null;
    }

    public ConsumidorDTO(Consumidor consumidor){
        this(consumidor.getIdConsumidor(), consumidor.getNome(), consumidor.getCpf_cnpj(), consumidor.getEmail(), consumidor.getCelular(), consumidor.getTelefone()
                , consumidor.getEndereco(), consumidor.getStatus().toString());
    }
}
