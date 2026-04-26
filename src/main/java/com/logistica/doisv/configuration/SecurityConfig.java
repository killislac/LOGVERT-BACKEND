package com.logistica.doisv.configuration;

import com.logistica.doisv.services.validacao.TokenAutenticacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private TokenAutenticacao tokenAutenticacao;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .headers(headers ->
                        headers.frameOptions(frameOptions -> frameOptions.disable()))
                .authorizeHttpRequests(auth -> auth

                        // ===================== ROTAS PÚBLICAS =====================
                        .requestMatchers(
                                "/logvert/login",
                                "/logvert/login/consumidores",
                                "/logvert/login/lojista/recuperar-senha",
                                "/logvert/login/lojista/validar-recuperacao",
                                "/logvert/login/lojista/atualizar-senha",
                                "/logvert/metricas/publicas",
                                "/logvert/feedbacks/lojas/{id}",
                                "/h2-console/**",
                                "/swagger-ui/**",
                                "swagger-ui.html",
                                "v3/api-docs/**",
                                "swagger-resources/**"
                        ).permitAll()

                        // ===================== ROTAS DELETE (ADMIN) =====================
                        .requestMatchers(HttpMethod.DELETE, "logvert/lojas/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/logvert/**").hasRole("ADMIN")

                        // ===================== ROTAS CONSUMIDOR =====================
                        .requestMatchers("/logvert/vendas/me").hasRole("CONSUMIDOR")
                        .requestMatchers("/logvert/solicitacoes/me").hasRole("CONSUMIDOR")
                        .requestMatchers(HttpMethod.PUT, "/logvert/solicitacoes/{id}").hasRole("CONSUMIDOR")
                        .requestMatchers("/logvert/solicitacoes/criar").hasRole("CONSUMIDOR")
                        .requestMatchers("/logvert/solicitacoes/cancelar/{id}").hasRole("CONSUMIDOR")
                        .requestMatchers(HttpMethod.POST, "/logvert/feedbacks").hasRole("CONSUMIDOR")

                        // ===================== ROTAS LOJISTA =====================
                        .requestMatchers(HttpMethod.GET, "/logvert/lojistas/profile").hasRole("LOJISTA")
                        .requestMatchers(HttpMethod.GET, "/logvert/solicitacoes").hasRole("LOJISTA")
                        .requestMatchers("/logvert/solicitacoes/aprovar/{id}").hasRole("LOJISTA")
                        .requestMatchers("/logvert/solicitacoes/reprovar/{id}").hasRole("LOJISTA")
                        .requestMatchers("/logvert/solicitacoes/atualizar/{id}").hasRole("LOJISTA")
                        .requestMatchers("/logvert/metricas/privadas").hasRole("LOJISTA")
                        .requestMatchers("/logvert/metricas/solicitacoes/por-status").hasRole("LOJISTA")
                        .requestMatchers("/logvert/produtos/**").hasRole("LOJISTA")
                        .requestMatchers("/logvert/consumidores/**").hasRole("LOJISTA")
                        .requestMatchers("/logvert/vendas/**").hasRole("LOJISTA")
                        .requestMatchers("/logvert/feedbacks/{id}").hasRole("LOJISTA")

                        // ===================== ROTAS LOJISTA OU CONSUMIDOR =====================
                        .requestMatchers(HttpMethod.GET, "/logvert/feedbacks/solicitacoes/{id}")
                                        .hasAnyRole("LOJISTA", "CONSUMIDOR")

                        // ===================== ROTAS MASTER =====================
                        .requestMatchers("/logvert/lojas/**").hasRole("MASTER")

                        // ===================== ROTAS ADMIN =====================
                        .requestMatchers(HttpMethod.POST, "logvert/lojistas").hasRole("ADMIN")
                        .requestMatchers("/logvert/lojistas/**").hasRole("ADMIN")

                        // ===================== DEMAIS ROTAS =====================
                        .anyRequest().authenticated()

                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(tokenAutenticacao, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}