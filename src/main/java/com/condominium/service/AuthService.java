package com.condominium.service;

import com.condominium.dto.LoginRequest;
import com.condominium.dto.LoginResponse;
import com.condominium.dto.UserDTO;
import com.condominium.model.Usuario;
import com.condominium.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final EmailService emailService;

    public AuthService(UsuarioRepository usuarioRepository, JwtService jwtService, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas"));

        if (!checkPassword(request.senha(), usuario.getSenha())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        if (!usuario.isAtivo()) {
            throw new RuntimeException("Usuário desativado");
        }

        String token = jwtService.generateToken(usuario);
        return new LoginResponse(token, UserDTO.fromEntity(usuario));
    }

    // Hash simples para senha (em produção usar BCrypt)
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao hashear senha", e);
        }
    }

    private boolean checkPassword(String rawPassword, String hashedPassword) {
        return hashPassword(rawPassword).equals(hashedPassword);
    }

    public String recuperarSenha(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("E-mail não encontrado em nossa base de dados."));

        // Gera senha temporária de 6 digitos aleatórios
        String novaSenha = String.format("%06d", new java.util.Random().nextInt(999999));
        usuario.setSenha(hashPassword(novaSenha));
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
