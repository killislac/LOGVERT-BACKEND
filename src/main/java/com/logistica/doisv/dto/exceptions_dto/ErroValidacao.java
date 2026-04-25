package com.logistica.doisv.dto.exceptions_dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ErroValidacao extends ErroCustomizado{
    private List<MensagemCampo> erros = new ArrayList<>();

    public ErroValidacao(Instant instante, Integer codigo, String mensagem, String url) {
        super(instante, codigo, mensagem, url);
    }

    public void adicionarErro(String nomeCampo, String mensagem) {
        erros.add(new MensagemCampo(nomeCampo, mensagem));
    }
}
