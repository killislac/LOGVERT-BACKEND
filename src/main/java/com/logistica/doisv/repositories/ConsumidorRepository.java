package com.logistica.doisv.repositories;

import com.logistica.doisv.dto.ConsumidorDTO;
import com.logistica.doisv.entities.Consumidor;
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
public interface ConsumidorRepository extends JpaRepository<Consumidor,Long>{
    List<Consumidor> findAllByLoja_idLoja(@Param("idLoja") Long idLoja);

    Optional<Consumidor> findByIdConsumidorAndLojaIdLoja(Long idConsumidor, Long idLoja);

    Optional<Consumidor> findByIdConsumidorAndStatusAndLojaIdLoja(Long idConsumidor, Status status, Long idLoja);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Consumidor c
        SET c.status = :status
        WHERE c.idConsumidor IN :idsConsumidores
        AND c.loja.idLoja = :idLoja
    """)
    int atualizarStatusConsumidores(@Param("idsConsumidores") List<Long> idsConsumidores,
                                    @Param("idLoja") Long idLoja,
                                    @Param("status") Status status);


    @Query("""
        SELECT c FROM Consumidor c
        JOIN FETCH c.loja l
        WHERE c.idConsumidor = :idConsumidor
        AND c.status = 'ATIVO'
        AND l.idLoja = :idLoja
    """)
    Optional<Consumidor> buscarConsumidorAtivo(@Param("idConsumidor") Long idConsumidor,
                                               @Param("idLoja") Long idLoja);

//    default Optional<Consumidor> buscarConsumidorAtivo(Long idConsumidor, Long idLoja){
//        return findByIdConsumidorAndStatusAndLojaIdLoja(idConsumidor, Status.ATIVO, idLoja);
//    }
}
