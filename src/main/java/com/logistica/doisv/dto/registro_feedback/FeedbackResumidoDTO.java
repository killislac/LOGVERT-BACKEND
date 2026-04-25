package com.logistica.doisv.dto.registro_feedback;

import com.logistica.doisv.entities.Feedback;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record FeedbackResumidoDTO(Long idSolicitacao,
                                  String nomeConsumidor,
                                  String comentario,
                                  Integer nota,
                                  String dataFeedback) {

    public FeedbackResumidoDTO(Feedback feedback){
        this(feedback.getSolicitacao().getId(),
                feedback.getConsumidor().getNome().split(" ")[0],
                feedback.getComentario(),
                feedback.getNota(),
                feedback.getDataFeedback()
                        .format(DateTimeFormatter
                                .ofPattern("dd/MM/yyyy", new Locale("pt", "BR"))));
    }
}
