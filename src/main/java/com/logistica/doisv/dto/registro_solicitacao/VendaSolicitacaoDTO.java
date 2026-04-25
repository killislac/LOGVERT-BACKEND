package com.logistica.doisv.dto.registro_solicitacao;

import com.logistica.doisv.entities.Venda;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record VendaSolicitacaoDTO(Long idVenda,
                                  String serial,
                                  BigDecimal precoTotal,
                                  String statusPedido,
                                  BigDecimal desconto,
                                  String formaPagamento,
                                  Integer prazoTroca,
                                  Integer prazoDevolucao,
                                  String dataCriacao,
                                  String dataEntrega) {

    public VendaSolicitacaoDTO(Venda venda){
        this(venda.getId(),
                venda.getSerialVenda(),
                venda.getPrecoTotal(),
                venda.getStatusPedido().getStatusPedido(),
                venda.getDesconto(),
                venda.getFormaPagamento(),
                venda.getPrazoTroca(),
                venda.getPrazoDevolucao(),
                venda.getDataCriacao().atZone(ZoneId.of("America/Sao_Paulo"))
                        .format(DateTimeFormatter
                                .ofPattern("dd/MM/yyyy HH:mm", new Locale("pt", "BR"))),
                venda.getDataEntrega().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
}
