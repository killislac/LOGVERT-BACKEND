package com.logistica.doisv.dto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record MetricasPublicasLojaDTO(UUID idLoja,
                                      String nomeLoja,
                                      String logo,
                                      String segmento,
                                      Integer totalAvaliacoes,
                                      Map<String, BigDecimal> notaMedia) {
}
