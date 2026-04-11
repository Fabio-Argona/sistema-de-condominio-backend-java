package com.condominium.controller;

import com.condominium.dto.OcorrenciaDTO;
import com.condominium.model.Ocorrencia;
import com.condominium.service.IOcorrenciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ocorrencias")
public class OcorrenciaController {

    private final IOcorrenciaService ocorrenciaService;

    public OcorrenciaController(IOcorrenciaService ocorrenciaService) {
        this.ocorrenciaService = ocorrenciaService;
    }

    @GetMapping
    public List<OcorrenciaDTO> listarTodas() {
        return ocorrenciaService.listarTodas();
    }

    @GetMapping("/morador/{moradorId}")
    public List<OcorrenciaDTO> listarPorMorador(@PathVariable Long moradorId) {
        return ocorrenciaService.listarPorMorador(moradorId);
    }

    @PostMapping("/morador/{moradorId}")
    public ResponseEntity<OcorrenciaDTO> criar(@PathVariable Long moradorId, @RequestBody Ocorrencia ocorrencia) {
        return ResponseEntity.ok(ocorrenciaService.criar(moradorId, ocorrencia));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OcorrenciaDTO> atualizarStatus(@PathVariable Long id, @RequestBody Ocorrencia dadosAtualizacao) {
        return ResponseEntity.ok(ocorrenciaService.atualizarStatus(id, dadosAtualizacao));
    }
}
