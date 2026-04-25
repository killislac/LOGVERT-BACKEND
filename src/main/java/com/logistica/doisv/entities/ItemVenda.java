package com.logistica.doisv.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.logistica.doisv.entities.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_Item_Venda")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemVenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(precision = 6, scale = 2)
    private BigDecimal precoOriginal;
    @Column(precision = 6, scale = 2)
    private BigDecimal precoVendido;
    @Column(scale = 2)
    private BigDecimal percentualVariacao;
    private Double quantidade;
    private String detalhes;
    @Enumerated(EnumType.STRING)
    private Status status = Status.ATIVO;

    @ManyToOne
    @JoinColumn(name = "idVenda")
    private Venda venda;

    @ManyToOne
    @JoinColumn(name = "idProduto")
    private Produto produto;

    @OneToMany(mappedBy = "itemVenda", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Solicitacao> solicitacoes = new ArrayList<>();

    public ItemVenda(BigDecimal precoOriginal, BigDecimal precoVendido, Double quantidade, String detalhes, Venda venda, Produto produto){
        this.precoOriginal = precoOriginal;
        this.precoVendido = precoVendido != null ? precoVendido : precoOriginal;
        this.quantidade = quantidade;
        this.detalhes = detalhes;
        this.venda = venda;
        this.produto = produto;
        percentualVariacao = this.precoVendido.multiply(BigDecimal.valueOf(100))
                                                .divide(this.precoOriginal, 4, RoundingMode.HALF_UP)
                                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
