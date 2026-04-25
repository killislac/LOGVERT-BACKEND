package com.logistica.doisv.entities;

import com.logistica.doisv.entities.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_recuperar_senha")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecuperarSenha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String codigoRecuperacao;
    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.ATIVO;

    @ManyToOne
    @JoinColumn(name = "idLojista")
    private Lojista lojista;

    public RecuperarSenha(String codigoRecuperacao, Lojista lojista){
        this.codigoRecuperacao = codigoRecuperacao;
        this.lojista = lojista;
    }

    public boolean isExpirado() {
        LocalDateTime dataExpiracao = this.dataCriacao.plusMinutes(15);
        return LocalDateTime.now().isAfter(dataExpiracao);
    }

    public boolean isAtivo() {
        return this.status.equals(Status.ATIVO);
    }
}
