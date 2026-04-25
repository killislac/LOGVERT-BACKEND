package com.logistica.doisv.dto.registro_feedback;

import com.logistica.doisv.entities.Feedback;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record FeedbackDTO(Long idFeedback,
                          @NotBlank(message = "O tipo de feedback é obrigatório.") String tipoFeedback,
                          @Min(value = 1, message = "A nota mínima é 1.")
                          @Max(value = 5, message = "A nota máxima é 5.") Integer nota,
                          String comentario,
                          String dataFeedback,
                          Long idConsumidor,
                          String nomeConsumidor,
                          @NotNull(message = "O ID da loja é obrigatório.") Long idLoja,
                          @NotNull(message = "O ID da solicitação é obrigatório.") Long idSolicitacao
                          ) {


    public FeedbackDTO(Feedback feedback){
        this(feedback.getIdFeedback(),
                feedback.getTipoFeedback().getDescricao(),
                feedback.getNota(),
                feedback.getComentario(),
                feedback.getDataFeedback()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("pt", "BR"))),
                feedback.getConsumidor().getIdConsumidor(),
                feedback.getConsumidor().getNome().split(" ")[0],
                feedback.getLoja().getIdLoja(),
                feedback.getSolicitacao().getId());
    }
}
