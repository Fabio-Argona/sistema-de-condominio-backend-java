package com.condominium.repository;

import com.condominium.model.AreaComum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaComumRepository extends JpaRepository<AreaComum, Long> {
}
