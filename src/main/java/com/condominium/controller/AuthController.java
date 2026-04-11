package com.condominium.controller;

import com.condominium.dto.LoginRequest;
import com.condominium.dto.LoginResponse;
import com.condominium.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Login e recuperação de senha")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
        summary = "Realizar login",
        description = "Autentica o usuário e retorna um token JWT válido por 7 dias.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido, token retornado"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content)
        }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            String ip = httpRequest.getHeader("X-Forwarded-For");
            if (ip == null || ip.isBlank()) ip = httpRequest.getRemoteAddr();
            LoginResponse response = authService.login(request, ip);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(
        summary = "Recuperar senha",
        description = "Gera uma nova senha temporária e envia por e-mail ao usuário.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Nova senha enviada por e-mail"),
            @ApiResponse(responseCode = "404", description = "E-mail não encontrado", content = @Content)
        }
    )
    @PostMapping("/recuperar-senha")
    public ResponseEntity<?> recuperarSenha(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.isBlank()) {
                throw new RuntimeException("E-mail é obrigatório para recuperar a senha");
            }
            
            authService.recuperarSenha(email);
            return ResponseEntity.ok(Map.of(
                "message", "Nova senha enviada para o e-mail informado!"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }
}