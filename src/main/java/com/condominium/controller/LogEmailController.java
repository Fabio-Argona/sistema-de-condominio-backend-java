package com.condominium.controller;

import com.condominium.dto.LogEmailResponse;
import com.condominium.repository.LogEmailRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/log-emails")
@CrossOrigin(origins = "*")
@Tag(name = "Log de E-mails", description = "Histórico de e-mails enviados pelo sistema")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('SINDICO')")
public class LogEmailController {

    @Autowired
    private LogEmailRepository logEmailRepository;

    @Operation(summary = "Listar todos os e-mails enviados (mais recentes primeiro)")
    @GetMapping
    public ResponseEntity<List<LogEmailResponse>> listarTodos() {
        List<LogEmailResponse> lista = logEmailRepository.findAllByOrderByDataHoraEnvioDesc()
                .stream().map(LogEmailResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Listar e-mails enviados para um destinatário específico")
    @GetMapping("/destinatario/{email}")
    public ResponseEntity<List<LogEmailResponse>> listarPorDestinatario(@PathVariable String email) {
        List<LogEmailResponse> lista = logEmailRepository.findByDestinatarioOrderByDataHoraEnvioDesc(email)
                .stream().map(LogEmailResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }
}
