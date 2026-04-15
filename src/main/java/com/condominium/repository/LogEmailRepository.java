package com.condominium.repository;

import com.condominium.model.LogEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogEmailRepository extends JpaRepository<LogEmail, Long> {
    List<LogEmail> findAllByOrderByDataHoraEnvioDesc();
    List<LogEmail> findByDestinatarioOrderByDataHoraEnvioDesc(String destinatario);
    List<LogEmail> findByTipoOrderByDataHoraEnvioDesc(LogEmail.TipoEmail tipo);
}
