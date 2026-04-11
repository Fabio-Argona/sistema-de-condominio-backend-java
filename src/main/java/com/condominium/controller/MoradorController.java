package com.condominium.controller;

import com.condominium.dto.UserDTO;
import com.condominium.model.Usuario;
import com.condominium.service.IMoradorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/moradores")
public class MoradorController {

    private final IMoradorService moradorService;

    public MoradorController(IMoradorService moradorService) {
        this.moradorService = moradorService;
    }

    @GetMapping
    public List<UserDTO> listarTodos() {
        return moradorService.listarTodos();
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Usuario morador) {
        return ResponseEntity.ok(moradorService.criar(morador));
    }

    @PostMapping("/{id}/reenviar-convite")
    public ResponseEntity<?> reenviarConvite(@PathVariable Long id) {
        Map<String, Object> resultado = moradorService.reenviarConvite(id);
        boolean success = Boolean.TRUE.equals(resultado.get("success"));
        return success ? ResponseEntity.ok(resultado) : ResponseEntity.status(500).body(resultado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> atualizar(@PathVariable Long id, @RequestBody Usuario moradorAtualizado) {
        return ResponseEntity.ok(moradorService.atualizar(id, moradorAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Long id) {
        return ResponseEntity.ok(moradorService.remover(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserDTO> alternarStatus(@PathVariable Long id) {
        return ResponseEntity.ok(moradorService.alternarStatus(id));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserDTO> alterarRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(moradorService.alterarRole(id, body.get("role")));
    }
}
