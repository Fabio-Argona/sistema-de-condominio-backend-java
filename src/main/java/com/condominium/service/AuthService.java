package com.condominium.service;

import com.condominium.dto.LoginRequest;
import com.condominium.dto.LoginResponse;
import com.condominium.dto.UserDTO;
import com.condominium.model.LogAcesso;
import com.condominium.model.Usuario;
import com.condominium.repository.LogAcessoRepository;
import com.condominium.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final LogAcessoRepository logAcessoRepository;

    public AuthService(UsuarioRepository usuarioRepository, JwtService jwtService, EmailService emailService, PasswordEncoder passwordEncoder, LogAcessoRepository logAcessoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.logAcessoRepository = logAcessoRepository;
    }

    public LoginResponse login(LoginRequest request, String ip) {
        System.out.println("Tentativa de login para: " + request.email());
        
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseGet(() -> {
                    System.out.println("Usuário não encontrado: " + request.email());
                    return null;
                });

        if (usuario == null) {
            throw new RuntimeException("Credenciais inválidas");
        }

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            System.out.println("Senha incorreta para o usuário: " + request.email());
            throw new RuntimeException("Credenciais inválidas");
        }

        if (!usuario.isAtivo()) {
            System.out.println("Usuário desativado: " + request.email());
            throw new RuntimeException("Usuário desativado");
        }

        LocalDateTime agora = LocalDateTime.now();
        usuario.setUltimoAcesso(agora);
        usuarioRepository.save(usuario);

        logAcessoRepository.save(new LogAcesso(
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getRole().name(),
            agora,
            ip
        ));

        System.out.println("Login bem-sucedido para: " + request.email() + " (Role: " + usuario.getRole() + ")");
        String token = jwtService.generateToken(usuario);
        return new LoginResponse(token, UserDTO.fromEntity(usuario));
    }

    public String recuperarSenha(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("E-mail não encontrado em nossa base de dados."));

        // Gera senha temporária de 6 digitos aleatórios
        String novaSenha = String.format("%06d", new java.util.Random().nextInt(999999));
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        // Envia e-mail real com a nova senha
        try {
            emailService.enviarNovaSenha(email, usuario.getNome(), novaSenha);
        } catch (Exception e) {
            // Se falhar o envio, desfaz a troca de senha e lança exceção
            usuario.setSenha(null); // ou recupere a senha antiga se desejar
            usuarioRepository.save(usuario);
            throw new RuntimeException("Falha ao enviar e-mail de recuperação: " + e.getMessage());
        }

        return novaSenha;
    }
}
