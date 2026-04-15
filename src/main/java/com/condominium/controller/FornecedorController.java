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

    @Operation(summary = "Listar fornecedores de um usuário")
    @GetMapping({"/usuario/{usuarioId}", "/morador/{usuarioId}"})
    @PreAuthorize("hasAnyRole('SINDICO', 'MORADOR')")
    public List<FornecedorDTO> listarPorMorador(@PathVariable Long usuarioId) {
        return fornecedorService.listarPorMorador(usuarioId);
    }

    @Operation(summary = "Cadastrar fornecedor (síndico)")
    @PostMapping
    public ResponseEntity<FornecedorDTO> criar(@RequestBody Fornecedor fornecedor) {
        return ResponseEntity.ok(fornecedorService.criar(fornecedor));
    }

    @Operation(summary = "Cadastrar fornecedor por usuário")
    @PostMapping({"/usuario/{usuarioId}", "/morador/{usuarioId}"})
    @PreAuthorize("hasAnyRole('SINDICO', 'MORADOR')")
    public ResponseEntity<FornecedorDTO> criarPorMorador(@PathVariable Long usuarioId, @RequestBody Fornecedor fornecedor) {
        return ResponseEntity.ok(fornecedorService.criarParaMorador(usuarioId, fornecedor));
    }

    @Operation(summary = "Atualizar fornecedor (síndico)")
    @PutMapping("/{id}")
    public ResponseEntity<FornecedorDTO> atualizar(@PathVariable Long id, @RequestBody Fornecedor dados) {
        return ResponseEntity.ok(fornecedorService.atualizar(id, dados));
    }

    @Operation(summary = "Atualizar fornecedor por usuário (somente dono)")
    @PutMapping({"/usuario/{usuarioId}/{id}", "/morador/{usuarioId}/{id}"})
    @PreAuthorize("hasAnyRole('SINDICO', 'MORADOR')")
    public ResponseEntity<FornecedorDTO> atualizarPorMorador(@PathVariable Long usuarioId, @PathVariable Long id, @RequestBody Fornecedor dados) {
        return ResponseEntity.ok(fornecedorService.atualizarParaMorador(usuarioId, id, dados));
    }

    @Operation(summary = "Deletar fornecedor (síndico)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        fornecedorService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletar fornecedor por usuário (somente dono)")
    @DeleteMapping({"/usuario/{usuarioId}/{id}", "/morador/{usuarioId}/{id}"})
    @PreAuthorize("hasAnyRole('SINDICO', 'MORADOR')")
    public ResponseEntity<Void> deletarPorMorador(@PathVariable Long usuarioId, @PathVariable Long id) {
        fornecedorService.deletarParaMorador(usuarioId, id);
        return ResponseEntity.noContent().build();
    }
}