package com.logistica.doisv.services;

import com.logistica.doisv.dto.MetricasPrivadasDTO;
import com.logistica.doisv.dto.MetricasPublicasLojaDTO;
import com.logistica.doisv.dto.registro_feedback.FeedbackResumidoDTO;
import com.logistica.doisv.entities.Feedback;
import com.logistica.doisv.entities.Loja;
import com.logistica.doisv.entities.Solicitacao;
import com.logistica.doisv.entities.enums.StatusSolicitacao;
import com.logistica.doisv.entities.enums.TipoFeedback;
import com.logistica.doisv.entities.enums.TipoSolicitacao;
import com.logistica.doisv.repositories.FeedbackRepository;
import com.logistica.doisv.repositories.LojaRepository;
import com.logistica.doisv.repositories.SolicitacaoRepository;
import com.logistica.doisv.util.conversao.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MetricaService {
    private static final String TIPO_LOJA = "loja";
    private static final String TIPO_SOLICITACAO = "solicitacao";
    private static final String TIPO_TROCA = "troca";
    private static final String TIPO_DEVOLUCAO = "devolucao";


    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private LojaRepository lojaRepository;

    public MetricasPrivadasDTO metricasPrivadasPorLojaEPeriodo(Long idLoja, Integer periodo){
        validarParametros(idLoja, periodo);

        LocalDate inicio = LocalDate.now().minusDays(periodo);
        LocalDate fim = LocalDate.now();

        List<Feedback> feedbacks = feedbackRepository.buscarFeedbacksPorLojaEPeriodo(idLoja, inicio, fim);

        if (feedbacks.isEmpty()) {
            throw new IllegalStateException("Nenhum feedback encontrado para o período informado");
        }

        Map<String, BigDecimal> percentuaisPorTipoSolicitacao = Map.of(
                TIPO_TROCA, calcularPercentualPorTipo(feedbacks, TipoSolicitacao.TROCA),
                TIPO_DEVOLUCAO, calcularPercentualPorTipo(feedbacks, TipoSolicitacao.DEVOLUCAO));

        Map<String, List<FeedbackResumidoDTO>> feedbacksPorTipo = Map.of(
                TIPO_LOJA, obterFeedbacksPorTipo(feedbacks, TipoFeedback.LOJA),
                TIPO_SOLICITACAO, obterFeedbacksPorTipo(feedbacks, TipoFeedback.SOLICITACAO));

        return MetricasPrivadasDTO.builder()
                .notaMedia(gerarAvaliacoes(feedbacks))
                .totalVendas(obterQuantidadeVendas(feedbacks))
                .quantidadeFeedbackPorTipo(calcularQuantidadePorTipoSolicitacao(feedbacks))
                .percentualPorTipo(percentuaisPorTipoSolicitacao)
                .avaliacoes(feedbacksPorTipo)
                .build();
    }

    public Map<String, Integer> obterQuantidadeSolicitacoes(Long idLoja, Integer periodo){
        validarParametros(idLoja, periodo);

        LocalDate inicio = LocalDate.now().minusDays(periodo);
        LocalDate fim = LocalDate.now();

        List<Solicitacao> solicitacoes = solicitacaoRepository.buscarSolicitacaoPorLojaEPeriodo(idLoja, inicio, fim);

        Map<String, Long> contagemPorStatus = solicitacoes.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getStatusSolicitacao().name().toLowerCase(),
                        Collectors.counting()
                ));

        Map<String, Integer> resultado = new HashMap<>();
        for(StatusSolicitacao status : StatusSolicitacao.values()){
            String chave = status.name().toLowerCase();
            resultado.put(chave, contagemPorStatus.getOrDefault(chave, 0L).intValue());
        }

        return resultado;
    }

    public Page<MetricasPublicasLojaDTO> obterMetricasPublicasTodasLojas(Pageable pageable, Integer periodo){
        LocalDate inicio = LocalDate.now().minusDays(periodo);
        LocalDate fim = LocalDate.now();

        Page<Loja> lojas = lojaRepository.findAll(pageable);

        List<Feedback> feedbacks = feedbackRepository.buscarFeedbacksPorPeriodo(inicio, fim);

        Map<Long, List<Feedback>> feedbacksPorLoja = feedbacks.stream()
                .collect(Collectors.groupingBy(f -> f.getLoja().getIdLoja()));

        return PaginacaoUtil.paraPagina(pageable, lojas.stream()
                .map(l -> {
                    List<Feedback> feedbacksDaLoja =
                            feedbacksPorLoja.getOrDefault(l.getIdLoja(), Collections.emptyList());

                    return new MetricasPublicasLojaDTO(
                            l.getIdPublico(),
                            l.getNome(),
                            l.getLogo(),
                            l.getSegmento(),
                            feedbacksDaLoja.size(),
                            gerarAvaliacoes(feedbacksDaLoja)
                    );
                }).toList());
    }

    private void validarParametros(Long idLoja, Integer periodo) {
        if (idLoja == null) {
            throw new IllegalArgumentException("ID da loja não pode ser nulo");
        }
        if (periodo == null || periodo <= 0) {
            throw new IllegalArgumentException("Período deve ser maior que zero");
        }
    }

    private Map<String, BigDecimal> gerarAvaliacoes(List<Feedback> feedbacks){
        return Map.of(
                TIPO_LOJA, calcularMediaFeedbacks(feedbacks, TipoFeedback.LOJA),
                TIPO_SOLICITACAO, calcularMediaFeedbacks(feedbacks, TipoFeedback.SOLICITACAO));
    }

    private BigDecimal calcularMediaFeedbacks(List<Feedback> feedbacks, TipoFeedback tipoFeedback){
        List<Feedback> feedbacksFiltrados =  feedbacks.stream()
                .filter(f -> f.getTipoFeedback() == tipoFeedback).toList();

        if(feedbacks.isEmpty() || feedbacksFiltrados.isEmpty()){
            return BigDecimal.ZERO;
        }

        double somaNota = feedbacksFiltrados.stream().mapToDouble(Feedback::getNota).sum();

        return BigDecimal.valueOf(somaNota)
                .divide(BigDecimal.valueOf(feedbacksFiltrados.size()), 2, RoundingMode.HALF_UP);

    }

    private Map<String, Integer> calcularQuantidadePorTipoSolicitacao(List<Feedback> feedbacks){
        return Map.ofEntries(
                Map.entry(TIPO_LOJA, (int) feedbacks.stream()
                        .filter(f -> f.getTipoFeedback() == TipoFeedback.LOJA)
                        .count()),
                Map.entry(TIPO_SOLICITACAO, (int) feedbacks.stream()
                        .filter(f -> f.getTipoFeedback() == TipoFeedback.SOLICITACAO)
                        .count())
        );
    }

    private BigDecimal calcularPercentualPorTipo(List<Feedback> feedbacks, TipoSolicitacao tipoSolicitacao){
        BigDecimal totalVendas = BigDecimal.valueOf(obterQuantidadeVendas(feedbacks));

        long totalSolicitacoes = feedbacks.stream()
                .map(Feedback::getSolicitacao)
                .distinct()
                .filter(s -> s.getTipoSolicitacao() == tipoSolicitacao)
                .count();

        return BigDecimal.valueOf(totalSolicitacoes)
                .multiply(BigDecimal.valueOf(100))
                .divide(totalVendas, 2, RoundingMode.HALF_UP);
    }

    private Integer obterQuantidadeVendas(List<Feedback> feedbacks){
        return feedbacks.stream().map(f -> f.getSolicitacao().getVenda()).toList().size();
    }

    private List<FeedbackResumidoDTO> obterFeedbacksPorTipo(List<Feedback> feedbacks, TipoFeedback tipoFeedback){
        return feedbacks.stream()
                .filter(f -> f.getTipoFeedback() == tipoFeedback)
                .map(FeedbackResumidoDTO::new)
                .collect(Collectors.toList());
    }

}
