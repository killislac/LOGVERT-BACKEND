    package com.logistica.doisv.entities;

    import com.logistica.doisv.entities.enums.Status;
    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDate;
    import java.util.UUID;

    @Entity
    @Table(name = "tb_licenca")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Licenca {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID idLicenca;
        private LocalDate validade;
        @Enumerated(EnumType.STRING)
        private Status status = Status.ATIVO;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "idLoja")
        private Loja loja;


        public void setStatus(Status status){
            this.status = status;
            this.loja.setStatus(status);
        }
    }
