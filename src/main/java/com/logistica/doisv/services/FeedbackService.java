package com.logistica.doisv.services;

import com.logistica.doisv.dto.AcessoDTO;
import com.logistica.doisv.dto.registro_feedback.FeedbackDTO;
import com.logistica.doisv.dto.registro_feedback.FeedbackResumidoDTO;
import com.logistica.doisv.entities.Consumidor;
import com.logistica.doisv.entities.Feedback;
import com.logistica.doisv.entities.enums.TipoFeedback;
import com.logistica.doisv.repositories.ConsumidorRepository;
import com.logistica.doisv.repositories.FeedbackRepository;
import com.logistica.doisv.services.exceptions.ResourceNotFoundException;
import com.logistica.doisv.util.conversao.PaginacaoUtil;
import com.logistica.doisv.util.validacao.FeedbackValidador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository repository;

    @Autowired
    private ConsumidorRepository consumidorRepository;

    @Autowired
    private FeedbackValidador feedbackValidador;


    @Transactional(readOnly = true)
    public FeedbackDTO buscarPorId(Long id, Long idLoja){
        Feedback feedback = repository.buscarFeedbackPorId(id, idLoja)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Feedback com ID %d não encontrado", id)));

        return new FeedbackDTO(feedback);
    }

    @Transactional(readOnly = true)
    public List<FeedbackDTO> buscarPorIdSolicitacao(Long idSolicitacao, AcessoDTO acesso){
        List<Feedback> feedbacks = repository.buscarFeedbacksPorIdSolicitacao(idSolicitacao, acesso.getIdLoja());

        if(feedbacks.isEmpty()){
            throw new ResourceNotFoundException(String.format("Solicitação de ID %d não encontrada.", idSolicitacao));
        }

        if(acesso.getIdConsumidor() != null){
            validarAcessoConsumidor(feedbacks, acesso.getIdConsumidor(), acesso.getIdVenda(), idSolicitacao);
        }

        return feedbacks.stream().map(FeedbackDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public Page<FeedbackResumidoDTO> buscarTodosPorLoja(Pageable pageable, UUID idPublicoLoja, Integer periodo){
        LocalDate inicio = LocalDate.now().minusDays(periodo);
        LocalDate fim = LocalDate.now();

        List<FeedbackResumidoDTO> feedbacks = repository.buscarFeedbacksPublicosPorLojaEPeriodo(idPublicoLoja, inicio, fim)
                .stream()
                .map(f -> new FeedbackResumidoDTO(null,
                        null,
                        f.getComentario(),
                        f.getNota(),
                        f.getDataFeedback().
                                format(DateTimeFormatter
                                .ofPattern("dd/MM/yyyy", new Locale("pt", "BR")))))
                .toList();

        return PaginacaoUtil.paraPagina(pageable, feedbacks);
    }

    @Transactional
    public FeedbackDTO salvar(FeedbackDTO dto, Long idConsumidor){
        Feedback feedback = new Feedback();

        criarFeedback(dto, feedback, idConsumidor);
        return new FeedbackDTO(repository.save(feedback));
    }


    private void criarFeedback(FeedbackDTO dto, Feedback entidade, Long idConsumidor){
        localizarConsumidor(entidade, idConsumidor);
        feedbackValidador.validarFeedback(dto, entidade);
        entidade.setTipoFeedback(TipoFeedback.deString(dto.tipoFeedback()));
        entidade.setNota(dto.nota());
        entidade.setComentario(dto.comentario());
    }

    private void localizarConsumidor(Feedback feedback, Long idConsumidor){
        Consumidor consumidor = consumidorRepository.findById(idConsumidor)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Consumidor ID %d não encontrado", idConsumidor)));
        feedback.setConsumidor(consumidor);
    }

    private void validarAcessoConsumidor(List<Feedback> feedbacks, Long idConsumidor, Long idVenda, Long idSolicitacao){
        boolean consumidorTemAcesso = feedbacks.stream()
                .allMatch(f -> f.getConsumidor() != null &&
                        Objects.equals(f.getConsumidor().getIdConsumidor(), idConsumidor) &&
                        Objects.equals(f.getSolicitacao().getVenda().getId(), idVenda));


        if(!consumidorTemAcesso){
            throw new ResourceNotFoundException(String.format("Solicitação de ID %d não encontrada.", idSolicitacao));
        }
    }
}
