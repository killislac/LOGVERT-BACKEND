package com.logistica.doisv.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.logistica.doisv.entities.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_Lojista", uniqueConstraints = @UniqueConstraint(columnNames = {"idLoja", "cpf"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lojista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLojista;
    private String nome;
    private String cpf;
    @Column(unique = true)
    private String email;
    @Column(name = "senha")
    private String password;
    @Enumerated(EnumType.STRING)
    private Status status = Status.ATIVO;
    @Column(nullable = false)
    private Boolean admin = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idLoja")
    private Loja loja;

    @OneToMany(mappedBy = "lojista", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<RecuperarSenha> recuperacoesSenha = new ArrayList<>();
}
