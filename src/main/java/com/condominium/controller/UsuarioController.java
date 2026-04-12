package com.condominium.controller;

import com.condominium.dto.UserDTO;
import com.condominium.model.Usuario;
import com.condominium.repository.UsuarioRepository;
import com.condominium.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Criação de acesso inicial")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Criar usuário inicial (público)", description = "Endpoint público para criação do primeiro acesso. Não requer autenticação.")
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "E-mail já cadastrado!"));
        }

        // Hasheia a senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        
        // Força role MORADOR para evitar escalada de privilégios via API pública
        usuario.setRole(Usuario.Role.MORADOR);

        // Garante que o usuário está ativo
        usuario.setAtivo(true);

        Usuario salvo = usuarioRepository.save(usuario);
        return ResponseEntity.ok(UserDTO.fromEntity(salvo));
    }

    @Operation(summary = "Trocar senha do próprio usuário", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{id}/senha")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> trocarSenha(@PathVariable Long id, @RequestBody Map<String, String> body) {
        var usuario = usuarioRepository.findById(id)
                .orElse(null);
        if (usuario == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Usuário não encontrado."));
        }

        String senhaAtual = body.get("senhaAtual");
        String novaSenha = body.get("novaSenha");

        if (senhaAtual == null || novaSenha == null || novaSenha.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "Nova senha deve ter no mínimo 6 caracteres."));
        }

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            return ResponseEntity.status(401).body(Map.of("message", "Senha atual incorreta."));
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setPrimeiroAcesso(false);
        usuarioRepository.save(usuario);
        String novoToken = jwtService.generateToken(usuario);
        return ResponseEntity.ok(Map.of(
            "message", "Senha alterada com sucesso!",
            "token", novoToken,
            "user", UserDTO.fromEntity(usuario)
        ));
    }
}
