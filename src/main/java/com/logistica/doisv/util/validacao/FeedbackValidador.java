package com.logistica.doisv.util.validacao;

import com.logistica.doisv.dto.registro_feedback.FeedbackDTO;
import com.logistica.doisv.entities.Feedback;
import com.logistica.doisv.entities.Loja;
import com.logistica.doisv.entities.Solicitacao;
import com.logistica.doisv.entities.enums.StatusSolicitacao;
import com.logistica.doisv.repositories.LojaRepository;
import com.logistica.doisv.repositories.SolicitacaoRepository;
import com.logistica.doisv.services.exceptions.RegraNegocioException;
import com.logistica.doisv.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeedbackValidador {
    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private LojaRepository lojaRepository;


    public void validarFeedback(FeedbackDTO dto, Feedback feedback){
        Solicitacao solicitacao = solicitacaoRepository.findById(dto.idSolicitacao())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Solicitação ID %d não encontrada", dto.idSolicitacao())));


        if(solicitacao.getStatusSolicitacao() != StatusSolicitacao.CONCLUIDA){
            throw new RegraNegocioException("A solicitação ainda está em andamento, aguarde ser concluída para dar um Feedback");
        }

        boolean feedbackExistente = solicitacao.getFeedbacks().stream()
                .anyMatch(f -> f.getTipoFeedback().getDescricao().equalsIgnoreCase(dto.tipoFeedback()));

        if(feedbackExistente){
            throw new RegraNegocioException(String.format("Já existe um Feedback para a solicitação ID %d", solicitacao.getId()));
        }

        if(!solicitacao.getConsumidor().getIdConsumidor().equals(feedback.getConsumidor().getIdConsumidor())){
            throw new RegraNegocioException("Consumidor divergente do consumidor registrado.");
        }

        Loja loja = lojaRepository.findById(dto.idLoja())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Loja ID %d não encontrada", dto.idLoja())));

        feedback.setLoja(loja);
        feedback.setSolicitacao(solicitacao);
    }

}
