package com.logistica.doisv.dto;

import com.logistica.doisv.dto.registro_feedback.FeedbackResumidoDTO;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Builder
public record MetricasPrivadasDTO(Map<String, BigDecimal> notaMedia,
                                  Integer totalVendas,
                                  Map<String, Integer> quantidadeFeedbackPorTipo,
                                  Map<String, BigDecimal> percentualPorTipo,
                                  Map<String, List<FeedbackResumidoDTO>> avaliacoes){
}
