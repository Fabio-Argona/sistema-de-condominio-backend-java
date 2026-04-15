package com.condominium.controller;

import com.condominium.dto.OcorrenciaDTO;
import com.condominium.model.Ocorrencia;
import com.condominium.service.impl.IOcorrenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ocorrencias")
@Tag(name = "Ocorrências", description = "Registro e acompanhamento de ocorrências")
@SecurityRequirement(name = "bearerAuth")
public class OcorrenciaController {

    private final IOcorrenciaService ocorrenciaService;

    public OcorrenciaController(IOcorrenciaService ocorrenciaService) {
        this.ocorrenciaService = ocorrenciaService;
    }

    @Operation(summary = "Listar todas as ocorrências")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<OcorrenciaDTO> listarTodas() {
        return ocorrenciaService.listarTodas();
    }

    @Operation(summary = "Listar ocorrências de um usuário")
    @GetMapping({"/usuario/{usuarioId}", "/morador/{usuarioId}"})
    @PreAuthorize("hasAnyRole('SINDICO', 'MORADOR')")
    public List<OcorrenciaDTO> listarPorMorador(@PathVariable Long usuarioId) {
        return ocorrenciaService.listarPorMorador(usuarioId);
    }

    @Operation(summary = "Listar ocorrências encaminhadas para um profissional")
    @GetMapping("/profissional/{profissionalId}")
    @PreAuthorize("hasAnyRole('SINDICO', 'MANTENEDOR')")
    public List<OcorrenciaDTO> listarPorProfissional(@PathVariable Long profissionalId) {
        return ocorrenciaService.listarPorProfissional(profissionalId);
    }

    @Operation(summary = "Abrir nova ocorrência")
    @PostMapping({"/usuario/{usuarioId}", "/morador/{usuarioId}"})
    @PreAuthorize("hasAnyRole('SINDICO', 'MORADOR', 'MANTENEDOR')")
    public ResponseEntity<OcorrenciaDTO> criar(@PathVariable Long usuarioId, @RequestBody Ocorrencia ocorrencia) {
        return ResponseEntity.ok(ocorrenciaService.criar(usuarioId, ocorrencia));
    }

    @Operation(summary = "Atualizar status e resposta da ocorrência")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SINDICO', 'MANTENEDOR')")
    public ResponseEntity<OcorrenciaDTO> atualizarStatus(@PathVariable Long id, @RequestBody Ocorrencia dadosAtualizacao) {
        return ResponseEntity.ok(ocorrenciaService.atualizarStatus(id, dadosAtualizacao));
    }
}