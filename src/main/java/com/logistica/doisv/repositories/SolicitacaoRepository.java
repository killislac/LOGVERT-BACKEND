package com.logistica.doisv.repositories;

import com.logistica.doisv.dto.registro_solicitacao.SolicitacaoResumidaDTO;
import com.logistica.doisv.entities.Solicitacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    @Query("""
    SELECT new com.logistica.doisv.dto.registro_solicitacao.SolicitacaoResumidaDTO(
        s.id, c.nome, v.id, s.tipoSolicitacao, s.motivo, s.dataSolicitacao, 
        s.dataAtualizacao, s.statusSolicitacao, s.status)
        FROM Solicitacao s
        JOIN s.consumidor c
        JOIN s.venda v
        WHERE v.loja.idLoja = :idLoja
        """)
    Page<SolicitacaoResumidaDTO> listarSolicitacoesResumidas(Pageable pageable, @Param("idLoja") Long idLoja);

    @Query("""
    SELECT s FROM Solicitacao s
    JOIN FETCH s.venda v
    JOIN FETCH s.consumidor c
    JOIN FETCH s.itemVenda iv
    JOIN FETCH iv.produto p
    LEFT JOIN FETCH s.anexos
    LEFT JOIN FETCH s.historicos
    WHERE s.id = :id
    """)
    Optional<Solicitacao> buscarCompletoPorId(@Param("id") Long id);


    @Query(value = """
            SELECT s.* FROM tb_solicitacao s
            JOIN tb_venda v
            ON s.id_venda = v.id
            WHERE v.id_loja = :idLoja
            AND DATE(s.data_Solicitacao) BETWEEN :inicio and :fim
            """, nativeQuery = true)
    List<Solicitacao> buscarSolicitacaoPorLojaEPeriodo(@Param("idLoja") Long idLoja,
                                                       @Param("inicio") LocalDate inicio,
                                                       @Param("fim") LocalDate fim);
}
