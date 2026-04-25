package com.logistica.doisv.dto.registro_venda.requisicao;

import com.logistica.doisv.entities.Solicitacao;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record RegistroVendaDTO(@DecimalMin(value = "0.0", message = "O desconto deve ser maior ou igual a 0,00.")
                               @Digits(integer = 6, fraction = 2, message = "O desconto deve ter no máximo 6 dígitos inteiros e 2 casas decimais.")
                               BigDecimal desconto,
                               @NotNull(message = "O ID do consumidor é obrigatório.") Long idConsumidor,
                               @PositiveOrZero(message = "O prazo de troca não pode ser negativo.")
                               Integer prazoTroca,
                               @PositiveOrZero(message = "O prazo de devolução não pode ser negativo.")
                               Integer prazoDevolucao,
                               String formaPagamento,
                               @NotBlank(message = "O status do pedido é obrigatório.") String statusPedido,
                               @NotEmpty(message = "A venda deve conter ao menos um item.") List<ItemDTO> itensVenda) {

    public RegistroVendaDTO(Solicitacao solicitacao, List<ItemDTO> novosProdutos){
        this(BigDecimal.valueOf(0),
                solicitacao.getConsumidor().getIdConsumidor(),
                solicitacao.getVenda().getPrazoTroca(),
                solicitacao.getVenda().getPrazoDevolucao(),
                String.format("Venda gerada a partir da %s ID: %d", solicitacao.getTipoSolicitacao().getDescricao(), solicitacao.getId()),
                "Entregue",
                novosProdutos);
    }
}
