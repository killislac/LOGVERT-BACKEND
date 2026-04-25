package com.logistica.doisv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcessoDTO {

    private List<TipoUsuario> permissoes;
    private String subject;
    private String nome;
    private Long idLoja;

    private Long idLojista;
    private Long idConsumidor;
    private String serialVenda;
    private Long idVenda;

    public enum TipoUsuario{
        LOJISTA,
        CONSUMIDOR,
        ADMIN,
        MASTER
    }

    public Collection<? extends GrantedAuthority> getPermissao(){
        if(this.permissoes == null){
            return Collections.emptyList();
        }

        return this.permissoes.stream()
                .map(permissao -> new SimpleGrantedAuthority("ROLE_" + permissao.name()))
                .toList();
    }
}
