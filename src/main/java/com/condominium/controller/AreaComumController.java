package com.condominium.controller;

import com.condominium.model.AreaComum;
import com.condominium.service.IAreaComumService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas-comuns")
public class AreaComumController {

    private final IAreaComumService areaComumService;

    public AreaComumController(IAreaComumService areaComumService) {
        this.areaComumService = areaComumService;
    }

    @GetMapping
    public List<AreaComum> listarTodas() {
        return areaComumService.listarTodas();
    }

    @PostMapping
    public ResponseEntity<AreaComum> criar(@RequestBody AreaComum area) {
        return ResponseEntity.ok(areaComumService.criar(area));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AreaComum> atualizar(@PathVariable Long id, @RequestBody AreaComum areaDados) {
        return ResponseEntity.ok(areaComumService.atualizar(id, areaDados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        areaComumService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
