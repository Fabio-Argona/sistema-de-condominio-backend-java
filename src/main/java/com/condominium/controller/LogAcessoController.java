package com.condominium.controller;

import com.condominium.model.LogAcesso;
import com.condominium.model.Usuario;
import com.condominium.repository.LogAcessoRepository;
import com.condominium.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/log-acessos")
@CrossOrigin(origins = "*")
@Tag(name = "Log de Acessos", description = "Auditoria de acessos ao sistema")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('SINDICO')")
public class LogAcessoController {

    private final LogAcessoRepository logAcessoRepository;
    private final UsuarioRepository usuarioRepository;

    public LogAcessoController(LogAcessoRepository logAcessoRepository, UsuarioRepository usuarioRepository) {
        this.logAcessoRepository = logAcessoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Operation(summary = "Listar todos os logs de acesso")
    @GetMapping
    public ResponseEntity<List<LogAcesso>> listarTodos() {
        return ResponseEntity.ok(logAcessoRepository.findAllByOrderByDataHoraDesc());
    }

    @Operation(summary = "Registrar acesso do usuário autenticado")
    @PostMapping
    public ResponseEntity<Void> registrarAcesso(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {

        if (userDetails == null) return ResponseEntity.status(401).build();

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (usuario == null) return ResponseEntity.status(401).build();

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();

        String pagina = body.getOrDefault("pagina", null);

        logAcessoRepository.save(new LogAcesso(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name(),
                LocalDateTime.now(),
                ip,
                pagina
        ));

        return ResponseEntity.ok().build();
    }
}