package com.condominium.controller;

import com.condominium.dto.ComunicadoDTO;
import com.condominium.model.Comunicado;
import com.condominium.service.impl.IComunicadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comunicados")
@Tag(name = "Comunicados", description = "Comunicados do condomínio")
@SecurityRequirement(name = "bearerAuth")
public class ComunicadoController {

    private final IComunicadoService comunicadoService;

    public ComunicadoController(IComunicadoService comunicadoService) {
        this.comunicadoService = comunicadoService;
    }

    @Operation(summary = "Listar todos os comunicados")
    @GetMapping
    public List<ComunicadoDTO> listarTodos() {
        return comunicadoService.listarTodos();
    }

    @Operation(summary = "Publicar novo comunicado")
    @PostMapping
    public ResponseEntity<ComunicadoDTO> criar(@RequestBody Comunicado comunicado) {
        return ResponseEntity.ok(comunicadoService.criar(comunicado));
    }

    @Operation(summary = "Atualizar comunicado")
    @PutMapping("/{id}")
    public ResponseEntity<ComunicadoDTO> atualizar(@PathVariable Long id, @RequestBody Comunicado comunicadoAtualizado) {
        return ResponseEntity.ok(comunicadoService.atualizar(id, comunicadoAtualizado));
    }

    @Operation(summary = "Remover comunicado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        comunicadoService.remover(id);
        return ResponseEntity.ok().build();
    }
}