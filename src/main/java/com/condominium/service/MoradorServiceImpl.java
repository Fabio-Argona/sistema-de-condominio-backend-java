package com.condominium.service;

import com.condominium.dto.UserDTO;
import com.condominium.exception.BusinessException;
import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.Usuario;
import com.condominium.repository.BoletoRepository;
import com.condominium.repository.OcorrenciaRepository;
import com.condominium.repository.ReservaRepository;
import com.condominium.repository.UsuarioRepository;
import com.condominium.service.impl.IMoradorService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MoradorServiceImpl implements IMoradorService {

    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final OcorrenciaRepository ocorrenciaRepository;
    private final ReservaRepository reservaRepository;
    private final BoletoRepository boletoRepository;
    private final PasswordEncoder passwordEncoder;

    public MoradorServiceImpl(UsuarioRepository usuarioRepository,
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

    @Override
    public List<UserDTO> listarTodos() {
        return usuarioRepository.findByRole(Usuario.Role.MORADOR).stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> criar(Usuario morador) {
        if (usuarioRepository.existsByEmail(morador.getEmail())) {
            throw new BusinessException("E-mail já está em uso!");
        }
        morador.setRole(Usuario.Role.MORADOR);
        morador.setAtivo(true);
        String senhaTemporaria = String.format("%06d", new java.security.SecureRandom().nextInt(999999));
        morador.setSenha(passwordEncoder.encode(senhaTemporaria));
        Usuario salvo = usuarioRepository.save(morador);

        boolean conviteEnviado = false;
        try {
            emailService.enviarConvite(salvo.getEmail(), salvo.getNome(), senhaTemporaria,
                    salvo.getApartamento(), salvo.getBloco());
            conviteEnviado = true;
        } catch (Exception e) {
            System.err.println("[MoradorService] Falha ao enviar convite para " + salvo.getEmail() + ": " + e.getMessage());
        }

        return Map.of(
                "morador", UserDTO.fromEntity(salvo),
                "conviteEnviado", conviteEnviado,
                "message", conviteEnviado
                        ? "Morador cadastrado e convite enviado por e-mail!"
                        : "Morador cadastrado, mas houve falha ao enviar o convite. Use o botão 'Reenviar Convite'."
        );
    }

    @Override
    public Map<String, Object> reenviarConvite(Long id) {
        var morador = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Morador", id));
        String novaSenha = String.format("%06d", new java.security.SecureRandom().nextInt(999999));
        morador.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(morador);

        try {
            emailService.enviarConvite(morador.getEmail(), morador.getNome(), novaSenha,
                    morador.getApartamento(), morador.getBloco());
            return Map.of(
                    "message", "Convite reenviado com sucesso para " + morador.getEmail() + "!",
                    "success", true
            );
        } catch (Exception e) {
            return Map.of(
                    "message", "Falha ao enviar o convite: " + e.getMessage(),
                    "success", false
            );
        }
    }

    @Override
    public UserDTO atualizar(Long id, Usuario moradorAtualizado) {
        var morador = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Morador", id));
        morador.setNome(moradorAtualizado.getNome());
        morador.setEmail(moradorAtualizado.getEmail());
        morador.setCpf(moradorAtualizado.getCpf());
        morador.setTelefone(moradorAtualizado.getTelefone());
        morador.setApartamento(moradorAtualizado.getApartamento());
        morador.setBloco(moradorAtualizado.getBloco());
        if (moradorAtualizado.getSenha() != null && !moradorAtualizado.getSenha().trim().isEmpty()) {
            morador.setSenha(passwordEncoder.encode(moradorAtualizado.getSenha()));
        }
        return UserDTO.fromEntity(usuarioRepository.save(morador));
    }

    @Override
    @Transactional
    public Map<String, Object> remover(Long id) {
        var morador = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Morador", id));
        ocorrenciaRepository.deleteByMoradorId(id);
        reservaRepository.deleteByMoradorId(id);
        boletoRepository.deleteByMoradorId(id);
        usuarioRepository.delete(morador);
        return Map.of("message", "Morador e todos os seus registros foram removidos com sucesso.");
    }

    @Override
    public UserDTO alternarStatus(Long id) {
        var morador = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Morador", id));
        morador.setAtivo(!morador.isAtivo());
        return UserDTO.fromEntity(usuarioRepository.save(morador));
    }

    @Override
    public UserDTO alterarRole(Long id, String roleStr) {
        if (roleStr == null || roleStr.isBlank()) {
            throw new BusinessException("O campo 'role' é obrigatório.");
        }
        Usuario.Role novaRole;
        try {
            novaRole = Usuario.Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Role inválida: " + roleStr);
        }
        var morador = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Morador", id));
        morador.setRole(novaRole);
        return UserDTO.fromEntity(usuarioRepository.save(morador));
    }
}
