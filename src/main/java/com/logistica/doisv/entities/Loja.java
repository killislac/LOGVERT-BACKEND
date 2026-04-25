    package com.logistica.doisv.entities;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import com.logistica.doisv.entities.enums.Status;
    import jakarta.persistence.*;
    import lombok.*;

    import java.util.*;

    @Entity
    @Table(name = "tb_Loja")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class Loja {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long idLoja;
        @Column(unique = true, nullable = false, updatable = false)
        private UUID idPublico = UUID.randomUUID();
        private String nome;
        private String cnpj;
        private String segmento;
        private String logo;
        @Column(unique = true)
        private String email;
        @Enumerated(EnumType.STRING)
        private Status status = Status.ATIVO;

        @OneToMany(mappedBy = "loja", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonIgnore
        private Set<Lojista> lojistas = new HashSet<>();

        @OneToMany(mappedBy = "loja", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonIgnore
        private List<Produto> produtos = new ArrayList<>();

        @OneToMany(mappedBy = "loja", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonIgnore
        private List<Consumidor> consumidores = new ArrayList<>();

        @OneToMany(mappedBy = "loja", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonIgnore
        private List<Venda> vendas = new ArrayList<>();

        @OneToMany(mappedBy = "loja", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonIgnore
        private List<Feedback> feedbacks = new ArrayList<>();

        @OneToMany(mappedBy = "loja", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonIgnore
        private List<Licenca> licencas = new ArrayList<>();

    }
