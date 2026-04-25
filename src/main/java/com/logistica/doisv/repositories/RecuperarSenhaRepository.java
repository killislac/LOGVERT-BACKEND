package com.logistica.doisv.repositories;

import com.logistica.doisv.entities.RecuperarSenha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecuperarSenhaRepository extends JpaRepository<RecuperarSenha, Long> {
    @Query(value = """
            SELECT r.* FROM tb_recuperar_senha r
            JOIN tb_lojista l
            ON l.id_lojista = r.id_lojista
            WHERE r.codigo_recuperacao = :codigo AND
            l.email = :email AND
            r.status = 'ATIVO'
            """, nativeQuery = true)
    Optional<RecuperarSenha> validarRecuperacao(@Param("email") String email,
                                                @Param("codigo") String codigo);

    @Modifying
    @Query(value = """
            UPDATE tb_recuperar_senha r
            JOIN tb_lojista l
            ON l.id_lojista = r.id_lojista
            SET r.status = 'INATIVO'
            WHERE l.email = :email AND
            r.codigo_recuperacao != :codigo
            """, nativeQuery = true)
    void cancelarCodigoRecuperacaoAnteriores(@Param("email") String email,
                                             @Param("codigo") String codigo);
}
