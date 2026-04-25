package com.logistica.doisv.services.validacao;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.logistica.doisv.dto.AcessoDTO;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenAutenticacao extends OncePerRequestFilter{

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest requisicao, HttpServletResponse resposta, FilterChain filterChain)throws ServletException, IOException {
        String uri = requisicao.getRequestURI();

        if(uri.contains("/login") ){
            filterChain.doFilter(requisicao, resposta);
            return;
        }

        String cabecalho = requisicao.getHeader("Authorization");

        if(cabecalho != null && cabecalho.startsWith("Bearer ")){
            String token = cabecalho.substring(7);

            try{
                AcessoDTO acesso = tokenService.validarToken(token);

                UsernamePasswordAuthenticationToken autenticacao =
                        new UsernamePasswordAuthenticationToken(acesso, null, acesso.getPermissao());

                autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(requisicao));

                SecurityContextHolder.getContext().setAuthentication(autenticacao);

            }catch(Exception e){
                resposta.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resposta.getWriter().write("Token inválido ou licença expirada");
                return;
            }
        }
        filterChain.doFilter(requisicao, resposta);
    }
}
