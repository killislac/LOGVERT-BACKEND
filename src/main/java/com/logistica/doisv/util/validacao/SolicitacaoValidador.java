package com.logistica.doisv.util.validacao;

import com.logistica.doisv.entities.Solicitacao;
import com.logistica.doisv.entities.Venda;
import com.logistica.doisv.entities.enums.Status;
import com.logistica.doisv.entities.enums.StatusPedido;
import com.logistica.doisv.entities.enums.StatusSolicitacao;
import com.logistica.doisv.entities.enums.TipoSolicitacao;
import com.logistica.doisv.services.exceptions.AssociacaoInvalidaException;
import com.logistica.doisv.services.exceptions.RegraNegocioException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class SolicitacaoValidador {

    public void validarStatusVenda(Venda venda, String tipo) {
        if (venda.getStatusPedido() != StatusPedido.ENTREGUE || venda.getStatus() == Status.INATIVO) {
            throw new RegraNegocioException(String.format("Não é possível realizar uma solicitação de %s com o status atual da venda", tipo));
        }
    }

    public void validarPrazoSolicitacao(Venda venda, TipoSolicitacao tipoSolicitacao) {
        int prazo = tipoSolicitacao == TipoSolicitacao.TROCA ? venda.getPrazoTroca() : venda.getPrazoDevolucao();

        LocalDate dataLimite = venda.getDataEntrega().plusDays(prazo);

        if (LocalDate.now().isAfter(dataLimite)) {
            throw new RegraNegocioException(String.format("Período para solicitar %s encerrou em %s",
                    tipoSolicitacao.getDescricao(),
                    dataLimite.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        }
    }

    public void validarQuantidade(Double quantidadeSolicitada, Double quantidadeComprada, String tipo) {
        if (quantidadeSolicitada > quantidadeComprada) {
            throw new RegraNegocioException(String.format("A quantidade selecionada para %s é maior que a quantidade comprada", tipo.toLowerCase()));
        }
    }

    public void validarLoja(Solicitacao solicitacao, Long idLoja){
        var validarLojaSolicitacao = solicitacao.getVenda().getLoja().getIdLoja().equals(idLoja);

        if (!validarLojaSolicitacao){
            throw new AssociacaoInvalidaException("Você não tem permissão para alterar essa solicitação.");
        }
    }

    public void validarAprovacaoSolicitacao(Solicitacao solicitacao, Long idLoja){
        var validarStatusSolicitacao = solicitacao.getStatusSolicitacao() != StatusSolicitacao.PENDENTE;
        var validarStatus = solicitacao.getStatus() == Status.INATIVO;

        validarLoja(solicitacao, idLoja);

        if (validarStatusSolicitacao || validarStatus){
            throw new RegraNegocioException(String.format("A solicitação de %s ID %s não pode mais ser alterada.",
                    solicitacao.getTipoSolicitacao().getDescricao().toLowerCase(),
                    solicitacao.getId()));
        }
    }

    public void validarAtualizacao(Solicitacao solicitacao, Long idLoja){
        validarLoja(solicitacao, idLoja);

        var validarStatus = solicitacao.getStatusSolicitacao() == StatusSolicitacao.PENDENTE ||
                solicitacao.getStatusSolicitacao() == StatusSolicitacao.REJEITADA || solicitacao.getStatusSolicitacao() == StatusSolicitacao.CONCLUIDA ||
                solicitacao.getStatus() == Status.INATIVO;

        if (validarStatus){
            throw new RegraNegocioException(String.format("A solicitação de %s ID %s não pode mais ser alterada no status atual: " +
                            "\nStatus Solicitação: %s" +
                            "\nStatus: %s.",
                    solicitacao.getTipoSolicitacao().getDescricao().toLowerCase(),
                    solicitacao.getId(),
                    solicitacao.getStatusSolicitacao().name(),
                    solicitacao.getStatus().name()));
        }
    }
    public void validarCancelamento(Solicitacao solicitacao, Long idLoja){
        validarLoja(solicitacao, idLoja);

        var validarStatus = solicitacao.getStatusSolicitacao() == StatusSolicitacao.EM_TRANSITO ||
                solicitacao.getStatusSolicitacao() == StatusSolicitacao.CONCLUIDA ||
                solicitacao.getStatusSolicitacao() == StatusSolicitacao.CANCELADA ||
                solicitacao.getStatus() == Status.INATIVO;

        if (validarStatus){
            throw new RegraNegocioException(String.format("A solicitação de %s ID %s não pode mais ser cancelada no status atual: " +
                            "\nStatus Solicitação: %s" +
                            "\nStatus: %s.",
                    solicitacao.getTipoSolicitacao().getDescricao().toLowerCase(),
                    solicitacao.getId(),
                    solicitacao.getStatusSolicitacao().name(),
                    solicitacao.getStatus().name()));
        }
    }
}
