package com.condominium.controller;

import com.condominium.dto.VisitanteDTO;
import com.condominium.model.Visitante;
import com.condominium.repository.VisitanteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/visitantes")
public class VisitanteController {

    private final VisitanteRepository visitanteRepository;

    public VisitanteController(VisitanteRepository visitanteRepository) {
        this.visitanteRepository = visitanteRepository;
    }

    @GetMapping
    public List<VisitanteDTO> listar() {
        return visitanteRepository.findAllByOrderByDataEntradaDesc().stream()
                .map(VisitanteDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<VisitanteDTO> criar(@RequestBody Visitante visitante) {
        visitante.setDataEntrada(LocalDateTime.now());
        Visitante salvo = visitanteRepository.save(visitante);
        return ResponseEntity.ok(VisitanteDTO.fromEntity(salvo));
    }

    @PatchMapping("/{id}/saida")
    public ResponseEntity<VisitanteDTO> registrarSaida(@PathVariable Long id) {
        return visitanteRepository.findById(id).map(visitante -> {
            visitante.setDataSaida(LocalDateTime.now());
            Visitante salvo = visitanteRepository.save(visitante);
            return ResponseEntity.ok(VisitanteDTO.fromEntity(salvo));
        }).orElse(ResponseEntity.notFound().build());
    }
}
