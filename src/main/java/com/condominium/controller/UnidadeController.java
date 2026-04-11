package com.condominium.controller;

import com.condominium.dto.UnidadeDTO;
import com.condominium.model.Unidade;
import com.condominium.service.IUnidadeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidades")
public class UnidadeController {

    private final IUnidadeService unidadeService;

    public UnidadeController(IUnidadeService unidadeService) {
        this.unidadeService = unidadeService;
    }

    @GetMapping
    public List<UnidadeDTO> listarTodas() {
        return unidadeService.listarTodas();
    }

    @PostMapping
    public ResponseEntity<UnidadeDTO> criar(@RequestBody Unidade unidade) {
        return ResponseEntity.ok(unidadeService.criar(unidade));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadeDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(unidadeService.buscarPorId(id));
    }
}
