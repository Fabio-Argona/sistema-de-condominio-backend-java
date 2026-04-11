package com.condominium.controller;

import com.condominium.dto.VisitanteDTO;
import com.condominium.model.Visitante;
import com.condominium.service.IVisitanteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitantes")
public class VisitanteController {

    private final IVisitanteService visitanteService;

    public VisitanteController(IVisitanteService visitanteService) {
        this.visitanteService = visitanteService;
    }

    @GetMapping
    public List<VisitanteDTO> listar() {
        return visitanteService.listar();
    }

    @PostMapping
    public ResponseEntity<VisitanteDTO> criar(@RequestBody Visitante visitante) {
        return ResponseEntity.ok(visitanteService.criar(visitante));
    }

    @PatchMapping("/{id}/saida")
    public ResponseEntity<VisitanteDTO> registrarSaida(@PathVariable Long id) {
        return ResponseEntity.ok(visitanteService.registrarSaida(id));
    }
}
