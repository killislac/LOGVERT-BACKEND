package com.logistica.doisv.dto;

import com.logistica.doisv.entities.Loja;
import com.logistica.doisv.util.validacao.CpfCnpj;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LojaDTO (Long idLoja,
                       @NotBlank(message = "O nome da loja é obrigatório.") String nome,
                       @CpfCnpj(message = "Informe um CNPJ válido.")
                       @NotNull(message = "O CNPJ é obrigatório.") String cnpj,
                       @NotBlank(message = "O segmento da loja é obrigatório.") String segmento,
                       String logo,
                       @Email(message = "Informe um e-mail válido.")
                       @NotBlank(message = "O e-mail da loja é obrigatório.") String email,
                       String status){

    public LojaDTO {
        cnpj = cnpj.replaceAll("[^0-9A-Za-z]", "").toUpperCase();
    }

    public LojaDTO(Loja loja) {
        this(loja.getIdLoja(), loja.getNome(), loja.getCnpj(), loja.getSegmento(), loja.getLogo(), loja.getEmail(), loja.getStatus().toString());
    }
}
