package com.logistica.doisv.dto.exceptions_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErroCustomizado{
    private Instant instante;
    private Integer codigo;
    private String mensagem;
    private String url;
}
