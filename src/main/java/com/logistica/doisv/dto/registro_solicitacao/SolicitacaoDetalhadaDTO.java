package com.logistica.doisv.dto.registro_solicitacao;

import com.logistica.doisv.dto.ConsumidorDTO;
import com.logistica.doisv.entities.AnexoSolicitacao;
import com.logistica.doisv.entities.Solicitacao;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public record SolicitacaoDetalhadaDTO(Long id,
                                      String tipo,
                                      Double quantidade,
                                      String motivo,
                                      String dataSolicitacao,
                                      String dataAtualizacao,
                                      String statusSolicitacao,
                                      String status,
                                      VendaSolicitacaoDTO venda,
                                      ConsumidorDTO consumidor,
                                      ProdutoSolicitacaoDTO produto,
                                      List<String> anexos,
                                      List<HistoricoSolicitacaoDTO> historico) {

    public SolicitacaoDetalhadaDTO(Solicitacao solicitacao) {
        this(solicitacao.getId(),
                solicitacao.getTipoSolicitacao().getDescricao(),
                solicitacao.getQuantidade(),
                solicitacao.getMotivo(),
                solicitacao.getDataSolicitacao().atZone(ZoneId.of("America/Sao_Paulo"))
                        .format(DateTimeFormatter
                                .ofPattern("dd/MM/yyyy HH:mm", new Locale("pt", "BR"))),
                solicitacao.getDataAtualizacao() != null ? solicitacao.getDataAtualizacao()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null,
                solicitacao.getStatusSolicitacao().getStatusSolicitacao(),
                solicitacao.getStatus().getStatusItem(),
                new VendaSolicitacaoDTO(solicitacao.getVenda()),
                new ConsumidorDTO(solicitacao.getConsumidor()),
                new ProdutoSolicitacaoDTO(solicitacao.getItemVenda()),
                solicitacao.getAnexos().stream().map(AnexoSolicitacao::getUrlImagem).collect(Collectors.toList()),
                solicitacao.getHistoricos().stream().map(HistoricoSolicitacaoDTO::new).toList());
    }
}
