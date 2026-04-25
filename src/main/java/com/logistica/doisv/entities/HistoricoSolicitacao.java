package com.logistica.doisv.entities;

import com.logistica.doisv.entities.enums.StatusSolicitacao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_Historico_Solicitacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricoSolicitacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private StatusSolicitacao statusAnterior;
    @Enumerated(EnumType.STRING)
    private StatusSolicitacao statusAtual;
    private LocalDateTime dataAtualizacao = LocalDateTime.now();
    private String observacao;

    @ManyToOne
    @JoinColumn(name = "idSolicitacao", nullable = false)
    private Solicitacao solicitacao;

}
