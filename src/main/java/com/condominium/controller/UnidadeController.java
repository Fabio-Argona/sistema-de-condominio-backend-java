package com.condominium.controller;

import com.condominium.dto.UnidadeDTO;
import com.condominium.model.Unidade;
import com.condominium.service.impl.IUnidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidades")
@Tag(name = "Unidades", description = "Gestão das unidades/apartamentos")
@SecurityRequirement(name = "bearerAuth")
public class UnidadeController {

    private final IUnidadeService unidadeService;

    public UnidadeController(IUnidadeService unidadeService) {
        this.unidadeService = unidadeService;
    }

    @Operation(summary = "Listar todas as unidades")
    @GetMapping
    public List<UnidadeDTO> listarTodas() {
        return unidadeService.listarTodas();
    }

    @Operation(summary = "Criar nova unidade")
    @PostMapping
    public ResponseEntity<UnidadeDTO> criar(@RequestBody Unidade unidade) {
        return ResponseEntity.ok(unidadeService.criar(unidade));
    }

    @Operation(summary = "Buscar unidade por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UnidadeDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(unidadeService.buscarPorId(id));
    }
}