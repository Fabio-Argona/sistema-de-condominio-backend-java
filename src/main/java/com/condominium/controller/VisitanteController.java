package com.condominium.controller;

import com.condominium.dto.VisitanteDTO;
import com.condominium.model.Visitante;
import com.condominium.service.impl.IVisitanteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitantes")
@Tag(name = "Visitantes", description = "Controle de acesso de visitantes")
@SecurityRequirement(name = "bearerAuth")
public class VisitanteController {

    private final IVisitanteService visitanteService;

    public VisitanteController(IVisitanteService visitanteService) {
        this.visitanteService = visitanteService;
    }

    @Operation(summary = "Listar todos os visitantes")
    @GetMapping
    public List<VisitanteDTO> listar() {
        return visitanteService.listar();
    }

    @Operation(summary = "Registrar entrada de visitante")
    @PostMapping
    public ResponseEntity<VisitanteDTO> criar(@RequestBody Visitante visitante) {
        return ResponseEntity.ok(visitanteService.criar(visitante));
    }

    @Operation(summary = "Registrar saída de visitante")
    @PatchMapping("/{id}/saida")
    public ResponseEntity<VisitanteDTO> registrarSaida(@PathVariable Long id) {
        return ResponseEntity.ok(visitanteService.registrarSaida(id));
    }
}
