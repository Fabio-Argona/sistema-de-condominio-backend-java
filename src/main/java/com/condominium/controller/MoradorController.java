package com.condominium.controller;

import com.condominium.dto.UserDTO;
import com.condominium.model.Usuario;
import com.condominium.service.impl.IMoradorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/moradores")
@Tag(name = "Moradores", description = "Gestão de moradores (SINDICO)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('SINDICO')")
public class MoradorController {

    private final IMoradorService moradorService;

    public MoradorController(IMoradorService moradorService) {
        this.moradorService = moradorService;
    }

    @Operation(summary = "Listar todos os moradores")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<UserDTO> listarTodos() {
        return moradorService.listarTodos();
    }

    @Operation(summary = "Cadastrar novo morador", description = "Cria o morador e envia e-mail de convite com senha temporária.")
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Usuario morador) {
        return ResponseEntity.ok(moradorService.criar(morador));
    }

    @Operation(summary = "Reenviar convite por e-mail")
    @PostMapping("/{id}/reenviar-convite")
    public ResponseEntity<?> reenviarConvite(@PathVariable Long id) {
        Map<String, Object> resultado = moradorService.reenviarConvite(id);
        boolean success = Boolean.TRUE.equals(resultado.get("success"));
        return success ? ResponseEntity.ok(resultado) : ResponseEntity.status(500).body(resultado);
    }

    @Operation(summary = "Atualizar dados do morador")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> atualizar(@PathVariable Long id, @RequestBody Usuario moradorAtualizado) {
        return ResponseEntity.ok(moradorService.atualizar(id, moradorAtualizado));
    }

    @Operation(summary = "Remover morador e todos os seus registros")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Long id) {
        return ResponseEntity.ok(moradorService.remover(id));
    }

    @Operation(summary = "Ativar ou desativar morador")
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserDTO> alternarStatus(@PathVariable Long id) {
        return ResponseEntity.ok(moradorService.alternarStatus(id));
    }

    @Operation(summary = "Alterar role do morador")
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserDTO> alterarRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(moradorService.alterarRole(id, body.get("role")));
    }
}