package com.logistica.doisv.dto.registro_venda.resposta;

import java.math.BigDecimal;

public interface VendaResumidaDTO {
    Long getIdVenda();
    String getNomeConsumidor();
    String getDataCriacao();
    BigDecimal getPrecoTotal();
    String getFormaPagamento();
    String getStatusPedido();
    String getStatus();
}
