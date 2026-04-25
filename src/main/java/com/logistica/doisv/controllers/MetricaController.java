    package com.logistica.doisv.controllers;

    import com.logistica.doisv.dto.AcessoDTO;
    import com.logistica.doisv.dto.MetricasPrivadasDTO;
    import com.logistica.doisv.dto.MetricasPublicasLojaDTO;
    import com.logistica.doisv.services.MetricaService;
    import com.logistica.doisv.services.validacao.TokenService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.bind.annotation.RestController;

    import java.util.Map;

    @RestController
    @RequestMapping("logvert/metricas")
    public class MetricaController {

        @Autowired
        private MetricaService metricaService;

        @Autowired
        private TokenService tokenService;

        @GetMapping("/privadas")
        public ResponseEntity<MetricasPrivadasDTO> buscarMetricasPrivadas(@RequestParam(defaultValue = "365") Integer periodo,
                                                                          @AuthenticationPrincipal AcessoDTO usuarioLogado){

            return ResponseEntity.ok(metricaService.metricasPrivadasPorLojaEPeriodo(usuarioLogado.getIdLoja(), periodo));
        }

        @GetMapping("/publicas")
        public ResponseEntity<Page<MetricasPublicasLojaDTO>> buscarMetricasPublicas(Pageable pageable, @RequestParam(defaultValue = "180") Integer periodo){
            return ResponseEntity.ok(metricaService.obterMetricasPublicasTodasLojas(pageable, periodo));
        }

        @GetMapping("/solicitacoes/por-status")
        public ResponseEntity<Map<String, Integer>> buscarQuantidadeSolicitacoesPorStatus(@RequestParam(value = "periodo", defaultValue = "365") Integer periodo,
                                                                                          @AuthenticationPrincipal AcessoDTO usuarioLogado){

            return ResponseEntity.ok(metricaService.obterQuantidadeSolicitacoes(usuarioLogado.getIdLoja(), periodo));
        }
    }
