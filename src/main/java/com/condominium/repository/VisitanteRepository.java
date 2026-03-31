package com.condominium.repository;

import com.condominium.model.Visitante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitanteRepository extends JpaRepository<Visitante, Long> {
    List<Visitante> findAllByOrderByDataEntradaDesc();
}
