package com.logistica.doisv.entities;

import com.logistica.doisv.entities.enums.Status;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_Anexo_Solicitacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnexoSolicitacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String urlImagem;
    @Enumerated(EnumType.STRING)
    private Status status = Status.ATIVO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idSolicitacao", nullable = false)
    private Solicitacao solicitacao;
}
