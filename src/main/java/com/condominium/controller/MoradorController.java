package com.condominium.controller;

import com.condominium.dto.UserDTO;
import com.condominium.model.Usuario;
import com.condominium.repository.BoletoRepository;
import com.condominium.repository.OcorrenciaRepository;
import com.condominium.repository.ReservaRepository;
import com.condominium.repository.UsuarioRepository;
import com.condominium.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/moradores")
public class MoradorController {

    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final OcorrenciaRepository ocorrenciaRepository;
    private final ReservaRepository reservaRepository;
    private final BoletoRepository boletoRepository;
    private final PasswordEncoder passwordEncoder;

    public MoradorController(
            UsuarioRepository usuarioRepository,
            EmailService emailService,
            OcorrenciaRepository ocorrenciaRepository,
            ReservaRepository reservaRepository,
            BoletoRepository boletoRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.reservaRepository = reservaRepository;
        this.boletoRepository = boletoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UserDTO> listarTodos() {
        return usuarioRepository.findByRole(Usuario.Role.MORADOR).stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Usuario morador) {
        if (usuarioRepository.existsByEmail(morador.getEmail())) {
            throw new RuntimeException("E-mail já está em uso!");
        }
        morador.setRole(Usuario.Role.MORADOR);
        morador.setAtivo(true);

        // Gera senha temporária de 6 dígitos automaticamente
        String senhaTemporaria = String.format("%06d", new java.util.Random().nextInt(999999));
        morador.setSenha(passwordEncoder.encode(senhaTemporaria));
        
        Usuario salvo = usuarioRepository.save(morador);

        // Envia convite por e-mail com as credenciais
        boolean conviteEnviado = false;
        try {
            emailService.enviarConvite(
                salvo.getEmail(),
                salvo.getNome(),
                senhaTemporaria,
                salvo.getApartamento(),
                salvo.getBloco()
            );
            conviteEnviado = true;
        } catch (Exception e) {
            System.err.println("[MoradorController] Falha ao enviar convite para " + salvo.getEmail() + ": " + e.getMessage());
        }

        return ResponseEntity.ok(Map.of(
            "morador", UserDTO.fromEntity(salvo),
            "conviteEnviado", conviteEnviado,
            "message", conviteEnviado 
                ? "Morador cadastrado e convite enviado por e-mail!" 
                : "Morador cadastrado, mas houve falha ao enviar o convite. Use o botão 'Reenviar Convite'."
        ));
    }

    @PostMapping("/{id}/reenviar-convite")
    public ResponseEntity<?> reenviarConvite(@PathVariable Long id) {
        return usuarioRepository.findById(id).map(morador -> {
            // Gera nova senha temporária
            String novaSenha = String.format("%06d", new java.util.Random().nextInt(999999));
            morador.setSenha(passwordEncoder.encode(novaSenha));
            usuarioRepository.save(morador);

            try {
                emailService.enviarConvite(
                    morador.getEmail(),
                    morador.getNome(),
                    novaSenha,
                    morador.getApartamento(),
                    morador.getBloco()
                );
                return ResponseEntity.ok(Map.of(
                    "message", "Convite reenviado com sucesso para " + morador.getEmail() + "!",
                    "success", true
                ));
            } catch (Exception e) {
                return ResponseEntity.status(500).body(Map.of(
                    "message", "Falha ao enviar o convite: " + e.getMessage(),
                    "success", false
                ));
            }
        }).orElse(ResponseEntity.notFound().build());
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
                morador.setSenha(passwordEncoder.encode(moradorAtualizado.getSenha()));
            }
            
            Usuario salvo = usuarioRepository.save(morador);
            return ResponseEntity.ok(UserDTO.fromEntity(salvo));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> remover(@PathVariable Long id) {
        return usuarioRepository.findById(id).map(morador -> {
            // Remove todos os registros relacionados antes de deletar o morador
            ocorrenciaRepository.deleteByMoradorId(id);
            reservaRepository.deleteByMoradorId(id);
            boletoRepository.deleteByMoradorId(id);
            
            usuarioRepository.delete(morador);
            return ResponseEntity.ok(Map.of(
                "message", "Morador e todos os seus registros foram removidos com sucesso."
            ));
        }).orElse(ResponseEntity.notFound().build());
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
