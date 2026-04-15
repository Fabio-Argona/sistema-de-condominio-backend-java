package com.condominium.service;

import com.condominium.dto.UserDTO;
import com.condominium.exception.BusinessException;
import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.Usuario;
import com.condominium.repository.BoletoRepository;
import com.condominium.repository.OcorrenciaRepository;
import com.condominium.repository.ReservaRepository;
import com.condominium.repository.UsuarioRepository;
import com.condominium.service.impl.IUsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final OcorrenciaRepository ocorrenciaRepository;
    private final ReservaRepository reservaRepository;
    private final BoletoRepository boletoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
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
        return usuarioRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> criar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new BusinessException("E-mail já está em uso!");
        }

        Usuario.Role role = usuario.getRole() != null ? usuario.getRole() : Usuario.Role.MORADOR;
        usuario.setRole(role);
        usuario.setAtivo(true);
        usuario.setPrimeiroAcesso(true);
        String senhaTemporaria = String.format("%06d", new java.security.SecureRandom().nextInt(999999));
        usuario.setSenha(passwordEncoder.encode(senhaTemporaria));
        Usuario salvo = usuarioRepository.save(usuario);

        boolean conviteEnviado = false;
        try {
            emailService.enviarConvite(salvo.getEmail(), salvo.getNome(), senhaTemporaria,
                    salvo.getApartamento(), salvo.getBloco());
            conviteEnviado = true;
        } catch (Exception e) {
            System.err.println("[UsuarioService] Falha ao enviar convite para " + salvo.getEmail() + ": " + e.getMessage());
        }

        return Map.of(
                "usuario", UserDTO.fromEntity(salvo),
                "conviteEnviado", conviteEnviado,
                "message", conviteEnviado
                ? "Usuário cadastrado e convite enviado por e-mail!"
                : "Usuário cadastrado, mas houve falha ao enviar o convite. Use o botão 'Reenviar Convite'."
        );
    }

    @Override
    public Map<String, Object> reenviarConvite(Long id) {
        var usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
        String novaSenha = String.format("%06d", new java.security.SecureRandom().nextInt(999999));
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        try {
            emailService.enviarConvite(usuario.getEmail(), usuario.getNome(), novaSenha,
                usuario.getApartamento(), usuario.getBloco());
            return Map.of(
                "message", "Convite reenviado com sucesso para " + usuario.getEmail() + "!",
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
    public UserDTO atualizar(Long id, Usuario usuarioAtualizado) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setCpf(usuarioAtualizado.getCpf());
        usuario.setTelefone(usuarioAtualizado.getTelefone());
        usuario.setApartamento(usuarioAtualizado.getApartamento());
        usuario.setBloco(usuarioAtualizado.getBloco());
        if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().trim().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
        }
        return UserDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public Map<String, Object> remover(Long id) {
        var usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
        ocorrenciaRepository.deleteByMoradorId(id);
        reservaRepository.deleteByMoradorId(id);
        boletoRepository.deleteByMoradorId(id);
        usuarioRepository.delete(usuario);
        return Map.of("message", "Usuário e todos os seus registros foram removidos com sucesso.");
    }

    @Override
    public UserDTO alternarStatus(Long id) {
        var usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
        usuario.setAtivo(!usuario.isAtivo());
        return UserDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Override
    public UserDTO alterarRole(Long id, String roleStr, String senhaConfirmacao, String emailUsuarioLogado) {
        if (roleStr == null || roleStr.isBlank()) {
            throw new BusinessException("O campo 'role' é obrigatório.");
        }
        Usuario.Role novaRole;
        try {
            novaRole = Usuario.Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Role inválida: " + roleStr);
        }

        if (novaRole == Usuario.Role.SINDICO) {
            if (senhaConfirmacao == null || senhaConfirmacao.isBlank()) {
                throw new BusinessException("Digite sua senha para promover um usuário a síndico.");
            }

            var usuarioLogado = usuarioRepository.findByEmail(emailUsuarioLogado)
                    .orElseThrow(() -> new BusinessException("Usuário autenticado não encontrado para confirmar a alteração."));

            if (!passwordEncoder.matches(senhaConfirmacao, usuarioLogado.getSenha())) {
                throw new BusinessException("Senha de confirmação inválida.");
            }
        }

        var usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
        usuario.setRole(novaRole);
        return UserDTO.fromEntity(usuarioRepository.save(usuario));
    }
}
