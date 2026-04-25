package com.logistica.doisv.repositories;

import com.logistica.doisv.entities.Loja;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LojaRepository extends JpaRepository<Loja, Long> {
    Page<Loja> findAll(Pageable pageable);
}
