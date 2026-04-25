package com.logistica.doisv.services.validacao;

import java.security.Key;
import java.util.*;

import com.logistica.doisv.entities.enums.Status;
import com.logistica.doisv.repositories.VendaRepository;
import com.logistica.doisv.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.logistica.doisv.dto.AcessoDTO;
import com.logistica.doisv.repositories.LojistaRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {
    
    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    private LojistaRepository lojistaRepository;

    @Autowired
    private VendaRepository vendaRepository;

    public AcessoDTO validarToken(String token){
        
        if(token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        
        try{
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
                Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build().parseClaimsJws(token)
                    .getBody();

            @SuppressWarnings("unchecked")
            List<AcessoDTO.TipoUsuario> permissoes = validarPermissoesUsuario(
                   claims.get("permissoes", ArrayList.class));

            AcessoDTO.AcessoDTOBuilder builder = AcessoDTO.builder()
                    .permissoes(permissoes)
                    .subject(claims.getSubject())
                    .nome(claims.get("nome", String.class))
                    .idLoja(claims.get("idLoja", Long.class));

            if(permissoes.contains(AcessoDTO.TipoUsuario.LOJISTA)){
                Long idLojista = claims.get("idLojista", Long.class);

                if(!lojistaRepository.existsByIdLojistaAndLoja_Status(idLojista, Status.ATIVO)){
                    throw new ResourceNotFoundException("Lojista do token não localizado");
                }

                return builder
                        .idLojista(claims.get("idLojista", Long.class))
                        .build();
            }
            else if(permissoes.contains(AcessoDTO.TipoUsuario.CONSUMIDOR)){
                if(!vendaRepository.existsById(claims.get("idVenda", Long.class))){
                    throw new ResourceNotFoundException("Venda do token não localizada");
                }

                return builder
                        .idConsumidor(claims.get("idConsumidor", Long.class))
                        .serialVenda(claims.get("serialVenda", String.class))
                        .idVenda(claims.get("idVenda", Long.class))
                        .build();
            }else {
                throw new ResourceNotFoundException("Tipo de usuário desconhecido no token.");
            }


        }catch(Exception e){
            throw new SecurityException("Token inválido ou experirado");
        }
    }

    private List<AcessoDTO.TipoUsuario> validarPermissoesUsuario(List<String> permissoes){
        if (permissoes.isEmpty()) {
            throw new SecurityException("Claim 'permissoes' não encontrada no token.");
        }

        List<AcessoDTO.TipoUsuario> permissoesUsuario = new ArrayList<>();

        for(String p : permissoes){
            permissoesUsuario.add(AcessoDTO.TipoUsuario.valueOf(p));
        }

        return permissoesUsuario;
    }
}
