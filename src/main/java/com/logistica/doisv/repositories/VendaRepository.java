package com.logistica.doisv.repositories;

import com.logistica.doisv.dto.registro_venda.resposta.VendaResumidaDTO;
import com.logistica.doisv.entities.Venda;
import com.logistica.doisv.entities.enums.Status;
import com.logistica.doisv.entities.enums.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    @EntityGraph(attributePaths = {"loja", "consumidor", "itensVenda", "itensVenda.produto"})
    Optional<Venda> findByIdAndLojaIdLoja(Long id, Long idLoja);

    @Query("SELECT " +
            "v.id AS idVenda, " +
            "c.nome AS nomeConsumidor, " +
            "FUNCTION('DATE_FORMAT', v.dataCriacao, '%d/%m/%Y') AS dataCriacao, " +
            "v.precoTotal AS precoTotal, " +
            "v.formaPagamento AS formaPagamento, " +
            "v.statusPedido AS statusPedido, " +
            "v.status AS status " +
            "FROM Venda v " +
            "LEFT JOIN v.consumidor c " +
            "LEFT JOIN v.loja l " +
            "WHERE l.idLoja = :idLoja")
    Page<VendaResumidaDTO> findVendasResumidasByLojaId(Pageable pageable, @Param("idLoja") Long idLoja);

    @EntityGraph(attributePaths = "consumidor")
    Optional<Venda> findBySerialVendaIgnoreCase(String serialVenda);

    @Query("""
            SELECT v FROM Venda v
            JOIN FETCH v.itensVenda i
            WHERE v.id = :id
            """)
    Optional<Venda> buscarVendaPorId(@Param("id")Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Venda v 
        SET v.status = :status
        WHERE v.loja.idLoja = :idLoja
        AND v.id IN :idsVendas
        AND v.statusPedido NOT IN :statusBloqueados
    """)
    int atualizarStatusVendas(@Param("idsVendas") List<Long> idsVendas,
                              @Param("idLoja") Long idLoja,
                              @Param("status") Status status,
                              @Param("statusBloqueados") List<StatusPedido> statusBloqueados);

}
