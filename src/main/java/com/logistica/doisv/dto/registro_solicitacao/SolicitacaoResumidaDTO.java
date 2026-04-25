package com.logistica.doisv.dto.registro_solicitacao;

import com.logistica.doisv.entities.Solicitacao;
import com.logistica.doisv.entities.enums.Status;
import com.logistica.doisv.entities.enums.StatusSolicitacao;
import com.logistica.doisv.entities.enums.TipoSolicitacao;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record SolicitacaoResumidaDTO(Long id,
                                     String consumidor,
                                     Long idVenda,
                                     String tipo,
                                     String motivo,
                                     String dataSolicitacao,
                                     String dataAtualizacao,
                                     String statusSolicitacao,
                                     String status
                                   ) {

    public SolicitacaoResumidaDTO(Long id, String nomeConsumidor, Long idVenda, TipoSolicitacao tipoEnum, String motivo,
                                  Instant dataSolicitacaoInstant, LocalDateTime dataAtualizacaoTime,
                                  StatusSolicitacao statusSolEnum, Status statusEnum) {
        this(id,
                nomeConsumidor,
                idVenda,
                tipoEnum.getDescricao(),
                motivo,
                dataSolicitacaoInstant.atZone(ZoneId.of("America/Sao_Paulo"))
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", new Locale("pt", "BR"))),
                dataAtualizacaoTime != null ?
                        dataAtualizacaoTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null,
                statusSolEnum.getStatusSolicitacao(),
                statusEnum.getStatusItem());
    }

    public SolicitacaoResumidaDTO(Solicitacao solicitacao){
        this(solicitacao.getId(),
                solicitacao.getConsumidor().getNome(),
                solicitacao.getVenda().getId(),
                solicitacao.getTipoSolicitacao().getDescricao(),
                solicitacao.getMotivo(),
                solicitacao.getDataSolicitacao().atZone(ZoneId.of("America/Sao_Paulo"))
                        .format(DateTimeFormatter
                                .ofPattern("dd/MM/yyyy HH:mm", new Locale("pt", "BR"))),
                solicitacao.getDataAtualizacao() != null ? solicitacao.getDataAtualizacao()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null,
                solicitacao.getStatusSolicitacao().getStatusSolicitacao(),
                solicitacao.getStatus().getStatusItem());
    }
}
