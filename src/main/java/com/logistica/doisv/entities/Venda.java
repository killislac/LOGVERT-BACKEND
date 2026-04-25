package com.logistica.doisv.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.logistica.doisv.entities.enums.Status;
import com.logistica.doisv.entities.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_Venda")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Venda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String serialVenda;
    private String senha;
    @Column(precision = 6, scale = 2)
    private BigDecimal precoTotal;
    @Enumerated(EnumType.STRING)
    private StatusPedido statusPedido;
    private BigDecimal desconto;
    private String formaPagamento;
    private Integer prazoTroca;
    private Integer prazoDevolucao;
    private Instant dataCriacao;
    private LocalDate dataEntrega;
    @Enumerated(EnumType.STRING)
    private Status status = Status.ATIVO;

    @ManyToOne
    @JoinColumn(name = "idLoja")
    private Loja loja;

    @ManyToOne
    @JoinColumn(name = "idConsumidor")
    private Consumidor consumidor;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemVenda> itensVenda = new ArrayList<>();

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Solicitacao> solicitacoes = new ArrayList<>();

    public Venda(Loja loja, Consumidor consumidor, String statusPedido, BigDecimal desconto, String formaPagamento, Integer prazoTroca, Integer prazoDevolucao){
        this.loja = loja;
        this.consumidor = consumidor;
        this.statusPedido = StatusPedido.converterDeStringParaEnum(statusPedido);
        this.desconto = desconto;
        this.formaPagamento = formaPagamento;
        this.prazoTroca = prazoTroca;
        this.prazoDevolucao = prazoDevolucao;
        this.precoTotal = BigDecimal.valueOf(0);
        this.dataCriacao = Instant.now();
    }

    public void calcularPrecoTotal(){
        BigDecimal precoTotalSemDesconto = this.itensVenda.stream()
                .map(item ->
                        item.getPrecoVendido()
                                .multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descontoAplicado = this.desconto != null ? this.desconto : BigDecimal.ZERO;

        this.precoTotal = precoTotalSemDesconto.subtract(descontoAplicado).setScale(2, RoundingMode.HALF_UP);
    }
}
