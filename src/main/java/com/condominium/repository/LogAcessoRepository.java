package com.condominium.repository;

import com.condominium.model.LogAcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LogAcessoRepository extends JpaRepository<LogAcesso, Long> {
    List<LogAcesso> findAllByOrderByDataHoraDesc();
    List<LogAcesso> findByUsuarioEmailOrderByDataHoraDesc(String email);
}
