package com.condominium.controller;

import com.condominium.dto.UserDTO;
import com.condominium.model.Usuario;
import com.condominium.service.impl.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/usuarios", "/api/moradores"})
@Tag(name = "Usuários", description = "Gestão de usuários (SINDICO)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('SINDICO')")
public class UsuarioController {

    private final IUsuarioService usuarioService;

    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Listar todos os usuários")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<UserDTO> listarTodos() {
        return usuarioService.listarTodos();
    }

    @Operation(summary = "Cadastrar novo usuário", description = "Cria o usuário e envia e-mail de convite com senha temporária.")
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.criar(usuario));
    }

    @Operation(summary = "Reenviar convite por e-mail")
    @PostMapping("/{id}/reenviar-convite")
    public ResponseEntity<?> reenviarConvite(@PathVariable Long id) {
        Map<String, Object> resultado = usuarioService.reenviarConvite(id);
        boolean success = Boolean.TRUE.equals(resultado.get("success"));
        return success ? ResponseEntity.ok(resultado) : ResponseEntity.status(500).body(resultado);
    }

    @Operation(summary = "Atualizar dados do usuário")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> atualizar(@PathVariable Long id, @RequestBody Usuario usuarioAtualizado) {
        return ResponseEntity.ok(usuarioService.atualizar(id, usuarioAtualizado));
    }

    @Operation(summary = "Remover usuário e todos os seus registros")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.remover(id));
    }

    @Operation(summary = "Ativar ou desativar usuário")
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserDTO> alternarStatus(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.alternarStatus(id));
    }

    @Operation(summary = "Alterar perfil do usuário")
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserDTO> alterarRole(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication authentication) {
        return ResponseEntity.ok(usuarioService.alterarRole(
                id,
                body.get("role"),
                body.get("senhaConfirmacao"),
                authentication.getName()
        ));
    }
}