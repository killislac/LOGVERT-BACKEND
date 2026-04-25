package com.logistica.doisv.repositories;

import com.logistica.doisv.entities.Produto;
import com.logistica.doisv.entities.enums.Status;
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
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    @Query(value = "SELECT p FROM Produto p JOIN FETCH p.loja l WHERE l.idLoja = :idLoja",
            countQuery = "SELECT COUNT(p) FROM Produto p WHERE p.loja.idLoja = :idLoja")
    Page<Produto> findAllByLojaComFetch(@Param("idLoja") Long idLoja, Pageable pageable);

    @EntityGraph(attributePaths = "loja")
    Optional<Produto> findByIdProdutoAndLojaIdLoja(Long idProduto, Long idLoja);

    boolean existsByDescricaoAndLojaIdLoja(String descricao, Long idLoja);

    boolean existsByDescricaoAndLojaIdLojaAndIdProdutoNot(String descricao, Long idLoja, Long idProduto);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Produto p
        SET p.status = :status
        WHERE p.idProduto in :idsProduto
        AND p.loja.idLoja = :idLoja""")
    int atualizarStatusProdutos(List<Long> idsProduto, Long idLoja, Status status);

    @Query("""
        SELECT p FROM Produto p
        WHERE p.idProduto IN :idsProdutos
        AND p.loja.idLoja = :idLoja""")
    List<Produto> buscarProdutoParaVenda(@Param("idsProdutos") List<Long> idsProdutos, @Param("idLoja") Long idLoja);
}
