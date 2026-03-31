package com.condominium.controller;

import com.condominium.dto.UserDTO;
import com.condominium.model.Usuario;
import com.condominium.repository.UsuarioRepository;
import com.condominium.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/moradores")
public class MoradorController {

    private final UsuarioRepository usuarioRepository;

    public MoradorController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<UserDTO> listarTodos() {
        return usuarioRepository.findByRole(Usuario.Role.MORADOR).stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<UserDTO> criar(@RequestBody Usuario morador) {
        if (usuarioRepository.existsByEmail(morador.getEmail())) {
            throw new RuntimeException("E-mail já está em uso!");
        }
        morador.setRole(Usuario.Role.MORADOR);
        // Hasheia a senha usando o mesmo método do login
        morador.setSenha(AuthService.hashPassword(morador.getSenha()));
        morador.setAtivo(true);
        
        Usuario salvo = usuarioRepository.save(morador);
        return ResponseEntity.ok(UserDTO.fromEntity(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> atualizar(@PathVariable Long id, @RequestBody Usuario moradorAtualizado) {
        return usuarioRepository.findById(id).map(morador -> {
            morador.setNome(moradorAtualizado.getNome());
            morador.setEmail(moradorAtualizado.getEmail());
            morador.setCpf(moradorAtualizado.getCpf());
            morador.setTelefone(moradorAtualizado.getTelefone());
            morador.setApartamento(moradorAtualizado.getApartamento());
            morador.setBloco(moradorAtualizado.getBloco());
            
            // Só atualiza a senha se foi enviada uma nova
            if (moradorAtualizado.getSenha() != null && !moradorAtualizado.getSenha().trim().isEmpty()) {
                morador.setSenha(AuthService.hashPassword(moradorAtualizado.getSenha()));
            }
            
            Usuario salvo = usuarioRepository.save(morador);
            return ResponseEntity.ok(UserDTO.fromEntity(salvo));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserDTO> alternarStatus(@PathVariable Long id) {
        return usuarioRepository.findById(id).map(morador -> {
            morador.setAtivo(!morador.isAtivo());
            Usuario salvo = usuarioRepository.save(morador);
            return ResponseEntity.ok(UserDTO.fromEntity(salvo));
        }).orElse(ResponseEntity.notFound().build());
    }
}
