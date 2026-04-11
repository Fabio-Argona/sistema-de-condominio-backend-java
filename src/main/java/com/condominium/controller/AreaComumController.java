package com.condominium.controller;

import com.condominium.model.AreaComum;
import com.condominium.service.impl.IAreaComumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas-comuns")
@Tag(name = "Áreas Comuns", description = "Gestão das áreas comuns")
@SecurityRequirement(name = "bearerAuth")
public class AreaComumController {

    private final IAreaComumService areaComumService;

    public AreaComumController(IAreaComumService areaComumService) {
        this.areaComumService = areaComumService;
    }

    @Operation(summary = "Listar todas as áreas comuns")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<AreaComum> listarTodas() {
        return areaComumService.listarTodas();
    }

    @Operation(summary = "Criar nova área comum")
    @PostMapping
    @PreAuthorize("hasRole('SINDICO')")
    public ResponseEntity<AreaComum> criar(@RequestBody AreaComum area) {
        return ResponseEntity.ok(areaComumService.criar(area));
    }

    @Operation(summary = "Atualizar área comum")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SINDICO')")
    public ResponseEntity<AreaComum> atualizar(@PathVariable Long id, @RequestBody AreaComum areaDados) {
        return ResponseEntity.ok(areaComumService.atualizar(id, areaDados));
    }

    @Operation(summary = "Deletar área comum")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SINDICO')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        areaComumService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}