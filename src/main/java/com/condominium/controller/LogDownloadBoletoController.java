package com.condominium.controller;

import com.condominium.dto.LogDownloadBoletoResponse;
import com.condominium.repository.LogDownloadBoletoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/log-downloads-boletos")
@CrossOrigin(origins = "*")
@Tag(name = "Log de Downloads de Boletos", description = "Histórico de downloads de boletos")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('SINDICO')")
public class LogDownloadBoletoController {

    private final LogDownloadBoletoRepository logDownloadBoletoRepository;

    public LogDownloadBoletoController(LogDownloadBoletoRepository logDownloadBoletoRepository) {
        this.logDownloadBoletoRepository = logDownloadBoletoRepository;
    }

    @Operation(summary = "Listar histórico de downloads de boletos")
    @GetMapping
    public ResponseEntity<List<LogDownloadBoletoResponse>> listarTodos() {
        List<LogDownloadBoletoResponse> lista = logDownloadBoletoRepository.findAllByOrderByDataHoraDownloadDesc()
                .stream()
                .map(LogDownloadBoletoResponse::new)
                .toList();
        return ResponseEntity.ok(lista);
    }
}