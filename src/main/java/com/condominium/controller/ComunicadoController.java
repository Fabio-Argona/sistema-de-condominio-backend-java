package com.condominium.controller;

import com.condominium.dto.ComunicadoDTO;
import com.condominium.model.Comunicado;
import com.condominium.service.IComunicadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comunicados")
public class ComunicadoController {

    private final IComunicadoService comunicadoService;

    public ComunicadoController(IComunicadoService comunicadoService) {
        this.comunicadoService = comunicadoService;
    }

    @GetMapping
    public List<ComunicadoDTO> listarTodos() {
        return comunicadoService.listarTodos();
    }

    @PostMapping
    public ResponseEntity<ComunicadoDTO> criar(@RequestBody Comunicado comunicado) {
        return ResponseEntity.ok(comunicadoService.criar(comunicado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComunicadoDTO> atualizar(@PathVariable Long id, @RequestBody Comunicado comunicadoAtualizado) {
        return ResponseEntity.ok(comunicadoService.atualizar(id, comunicadoAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        comunicadoService.remover(id);
        return ResponseEntity.ok().build();
    }
}
