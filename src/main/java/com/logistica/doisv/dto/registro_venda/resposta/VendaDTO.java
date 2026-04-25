package com.logistica.doisv.dto.registro_venda.resposta;

import com.logistica.doisv.entities.Venda;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public record VendaDTO (Long idVenda,
                        String serial,
                        BigDecimal precoTotal,
                        String statusPedido,
                        BigDecimal desconto,
                        String formaPagamento,
                        Integer prazoTroca,
                        Integer prazoDevolucao,
                        String dataCriacao,
                        String dataEntrega,
                        LojaVendaDTO loja,
                        ConsumidorVendaDTO consumidor,
                        String status,
                        List<ItemVendaDTO> itens) {

    public VendaDTO(Venda venda){
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
                                .ofPattern("dd/MM/yyyy", new Locale("pt", "BR"))),
                venda.getDataEntrega() != null ? venda.getDataEntrega()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "",
                new LojaVendaDTO(venda.getLoja()),
                new ConsumidorVendaDTO(venda.getConsumidor()),
                venda.getStatus().toString(),
                venda.getItensVenda().stream().map(ItemVendaDTO::new).toList());
    }
}
