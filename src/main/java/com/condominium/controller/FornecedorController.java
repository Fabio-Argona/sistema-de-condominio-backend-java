package com.condominium.controller;

import com.condominium.dto.FornecedorDTO;
import com.condominium.model.Fornecedor;
import com.condominium.service.impl.IFornecedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fornecedores")
@PreAuthorize("hasRole('SINDICO')")
@Tag(name = "Fornecedores", description = "Cadastro de fornecedores")
@SecurityRequirement(name = "bearerAuth")
public class FornecedorController {

    private final IFornecedorService fornecedorService;

    public FornecedorController(IFornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @Operation(summary = "Listar todos os fornecedores")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<FornecedorDTO> listarTodos() {
        return fornecedorService.listarTodos();
    }

    @Operation(summary = "Listar fornecedores de um morador")
    @GetMapping("/morador/{moradorId}")
    @PreAuthorize("hasAnyRole('SINDICO', 'MORADOR')")
    public List<FornecedorDTO> listarPorMorador(@PathVariable Long moradorId) {
        return fornecedorService.listarPorMorador(moradorId);
    }

    @Operation(summary = "Cadastrar fornecedor (síndico)")
    @PostMapping
    public ResponseEntity<FornecedorDTO> criar(@RequestBody Fornecedor fornecedor) {
        return ResponseEntity.ok(fornecedorService.criar(fornecedor));
    }

    @Operation(summary = "Cadastrar fornecedor por morador")
    @PostMapping("/morador/{moradorId}")
    @PreAuthorize("hasAnyRole('SINDICO', 'MORADOR')")
    public ResponseEntity<FornecedorDTO> criarPorMorador(@PathVariable Long moradorId, @RequestBody Fornecedor fornecedor) {
        return ResponseEntity.ok(fornecedorService.criarParaMorador(moradorId, fornecedor));
    }

    @Operation(summary = "Atualizar fornecedor (síndico)")
    @PutMapping("/{id}")
    public ResponseEntity<FornecedorDTO> atualizar(@PathVariable Long id, @RequestBody Fornecedor dados) {
        return ResponseEntity.ok(fornecedorService.atualizar(id, dados));
    }

    @Operation(summary = "Atualizar fornecedor por morador (somente dono)")
    @PutMapping("/morador/{moradorId}/{id}")
    @PreAuthorize("hasAnyRole('SINDICO', 'MORADOR')")
    public ResponseEntity<FornecedorDTO> atualizarPorMorador(@PathVariable Long moradorId, @PathVariable Long id, @RequestBody Fornecedor dados) {
        return ResponseEntity.ok(fornecedorService.atualizarParaMorador(moradorId, id, dados));
    }

    @Operation(summary = "Deletar fornecedor (síndico)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        fornecedorService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletar fornecedor por morador (somente dono)")
    @DeleteMapping("/morador/{moradorId}/{id}")
    @PreAuthorize("hasAnyRole('SINDICO', 'MORADOR')")
    public ResponseEntity<Void> deletarPorMorador(@PathVariable Long moradorId, @PathVariable Long id) {
        fornecedorService.deletarParaMorador(moradorId, id);
        return ResponseEntity.noContent().build();
    }
}