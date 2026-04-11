package com.condominium.repository;

import com.condominium.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    List<Fornecedor> findAllByOrderByNomeAsc();
    List<Fornecedor> findByMoradorIdOrderByNomeAsc(Long moradorId);
}
