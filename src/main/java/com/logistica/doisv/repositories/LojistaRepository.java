package com.logistica.doisv.repositories;

import com.logistica.doisv.dto.LojistaDTO;
import com.logistica.doisv.entities.Lojista;
import com.logistica.doisv.entities.enums.Status;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LojistaRepository extends JpaRepository<Lojista,Long> {
    @EntityGraph(attributePaths = "loja")
    Optional<Lojista> findByEmail(String email);

    @Query("""
       SELECT new com.logistica.doisv.dto.LojistaDTO(
           l.idLojista, l.nome, l.cpf, l.email, '', l.loja.idLoja, CAST(l.status AS string))
       FROM Lojista l 
       WHERE l.loja.idLoja = :idLoja
       """)
    List<LojistaDTO> buscarLojistasDTOPorLoja(@Param("idLoja") Long idLoja);

    @Query("SELECT l FROM Lojista l " +
            "WHERE l.idLojista = :idLojista " +
            "AND l.loja.idLoja = :idLoja")
    Optional<Lojista> findByIdLojistaAndLojaIdLoja(@Param("idLojista") Long idLojista,
                                                   @Param("idLoja") Long idLoja);

    boolean existsByIdLojistaAndLoja_Status(Long idLojista, Status lojaStatus);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Lojista l
        SET l.status = :status
        WHERE l.idLojista IN :idsLojistas
        AND l.loja.idLoja = :idLoja
    """)
    int atualizarStatusLojistas(@Param("idsLojistas") List<Long> idsLojistas,
                                @Param("idLoja") Long idLoja,
                                @Param("status") Status status);
}
