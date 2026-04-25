package com.logistica.doisv.repositories;

import com.logistica.doisv.entities.Licenca;
import com.logistica.doisv.entities.enums.Status;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LicencaRepository extends JpaRepository<Licenca, UUID> {

    @EntityGraph(attributePaths = "loja")
    List<Licenca> findAllByStatus(Status status);
}
