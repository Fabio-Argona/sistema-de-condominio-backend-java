package com.condominium.repository;

import com.condominium.model.Ocorrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {
    List<Ocorrencia> findAllByOrderByDataCriacaoDesc();
    List<Ocorrencia> findByMoradorIdOrderByDataCriacaoDesc(Long moradorId);
    void deleteByMoradorId(Long moradorId);
}
