package com.condominium.controller;

import com.condominium.dto.FornecedorDTO;
import com.condominium.model.Fornecedor;
import com.condominium.service.IFornecedorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorController {

    private final IFornecedorService fornecedorService;

    public FornecedorController(IFornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @GetMapping
    public List<FornecedorDTO> listarTodos() {
        return fornecedorService.listarTodos();
    }

    @GetMapping("/morador/{moradorId}")
    public List<FornecedorDTO> listarPorMorador(@PathVariable Long moradorId) {
        return fornecedorService.listarPorMorador(moradorId);
    }

    @PostMapping
    public ResponseEntity<FornecedorDTO> criar(@RequestBody Fornecedor fornecedor) {
        return ResponseEntity.ok(fornecedorService.criar(fornecedor));
    }

    @PostMapping("/morador/{moradorId}")
    public ResponseEntity<FornecedorDTO> criarPorMorador(@PathVariable Long moradorId, @RequestBody Fornecedor fornecedor) {
        return ResponseEntity.ok(fornecedorService.criarParaMorador(moradorId, fornecedor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FornecedorDTO> atualizar(@PathVariable Long id, @RequestBody Fornecedor dados) {
        return ResponseEntity.ok(fornecedorService.atualizar(id, dados));
    }

    @PutMapping("/morador/{moradorId}/{id}")
    public ResponseEntity<FornecedorDTO> atualizarPorMorador(@PathVariable Long moradorId, @PathVariable Long id, @RequestBody Fornecedor dados) {
        return ResponseEntity.ok(fornecedorService.atualizarParaMorador(moradorId, id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        fornecedorService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/morador/{moradorId}/{id}")
    public ResponseEntity<Void> deletarPorMorador(@PathVariable Long moradorId, @PathVariable Long id) {
        fornecedorService.deletarParaMorador(moradorId, id);
        return ResponseEntity.noContent().build();
    }
}
