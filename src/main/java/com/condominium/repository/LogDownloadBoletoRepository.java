package com.condominium.repository;

import com.condominium.model.LogDownloadBoleto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogDownloadBoletoRepository extends JpaRepository<LogDownloadBoleto, Long> {
    List<LogDownloadBoleto> findAllByOrderByDataHoraDownloadDesc();
}