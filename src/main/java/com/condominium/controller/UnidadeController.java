package com.condominium.controller;

import com.condominium.dto.UnidadeDTO;
import com.condominium.model.Unidade;
import com.condominium.repository.UnidadeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/unidades")
public class UnidadeController {

    private final UnidadeRepository unidadeRepository;

    public UnidadeController(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

    @GetMapping
    public List<UnidadeDTO> listarTodas() {
        return unidadeRepository.findAll().stream()
                .map(UnidadeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<UnidadeDTO> criar(@RequestBody Unidade unidade) {
        // Inicialmente cadastra apenas os dados básicos ("numero" e "bloco")
        Unidade salva = unidadeRepository.save(unidade);
        return ResponseEntity.ok(UnidadeDTO.fromEntity(salva));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UnidadeDTO> buscarPorId(@PathVariable Long id) {
        return unidadeRepository.findById(id)
                .map(unidade -> ResponseEntity.ok(UnidadeDTO.fromEntity(unidade)))
                .orElse(ResponseEntity.notFound().build());
    }
}
