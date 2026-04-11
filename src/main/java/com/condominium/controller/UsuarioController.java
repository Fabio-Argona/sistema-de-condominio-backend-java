package com.condominium.controller;

import com.condominium.dto.UserDTO;
import com.condominium.model.Usuario;
import com.condominium.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Criação de acesso inicial")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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
}
