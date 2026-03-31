package com.condominium.repository;

import com.condominium.model.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoletoRepository extends JpaRepository<Boleto, Long> {
    List<Boleto> findByMoradorIdOrderByDataVencimentoDesc(Long moradorId);
}
