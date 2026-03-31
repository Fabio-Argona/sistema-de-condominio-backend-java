package com.condominium.repository;

import com.condominium.model.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnidadeRepository extends JpaRepository<Unidade, Long> {
    List<Unidade> findByBlocoAndNumero(String bloco, String numero);
    List<Unidade> findByMoradorId(Long moradorId);
    List<Unidade> findByProprietarioId(Long proprietarioId);
}
