package com.condominium.controller;

import com.condominium.dto.BoletoRequest;
import com.condominium.dto.BoletoResponse;
import com.condominium.service.impl.IBoletoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boletos")
@CrossOrigin(origins = "*")
@Tag(name = "Boletos", description = "Geração e gestão de boletos")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('SINDICO')")
public class BoletoController {

    @Autowired
    private IBoletoService boletoService;

    @Operation(summary = "Listar todos os boletos")
    @GetMapping
    public ResponseEntity<List<BoletoResponse>> listarTodos() {
        return ResponseEntity.ok(boletoService.listarBoletos());
    }

    @Operation(summary = "Listar boletos por morador")
    @GetMapping("/morador/{moradorId}")
    @PreAuthorize("hasAnyRole('SINDICO', 'MORADOR')")
    public ResponseEntity<List<BoletoResponse>> listarPorMorador(@PathVariable Long moradorId) {
        return ResponseEntity.ok(boletoService.listarBoletosPorMorador(moradorId));
    }

    @Operation(summary = "Gerar novo boleto")
    @PostMapping
    public ResponseEntity<BoletoResponse> criar(@RequestBody BoletoRequest request) {
        return ResponseEntity.ok(boletoService.gerarBoleto(request));
    }

    @Operation(summary = "Atualizar boleto")
    @PutMapping("/{id}")
    public ResponseEntity<BoletoResponse> atualizar(@PathVariable Long id, @RequestBody BoletoRequest request) {
        return ResponseEntity.ok(boletoService.atualizarBoleto(id, request));
    }

    @Operation(summary = "Registrar pagamento de boleto")
    @PutMapping("/{id}/pagar")
    @PreAuthorize("hasAnyRole('SINDICO', 'MORADOR')")
    public ResponseEntity<BoletoResponse> pagar(@PathVariable Long id) {
        return ResponseEntity.ok(boletoService.pagarBoleto(id));
    }

    @Operation(summary = "Deletar boleto")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        boletoService.deletarBoleto(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Enviar boleto por e-mail")
    @PostMapping("/{id}/enviar-email")
    public ResponseEntity<Map<String, String>> enviarEmail(@PathVariable Long id) {
        boletoService.enviarEmailBoleto(id);
        return ResponseEntity.ok(Map.of("message", "E-mail enviado com sucesso!"));
    }

    @Operation(summary = "Enviar cobrança por e-mail")
    @PostMapping("/{id}/enviar-cobranca")
    public ResponseEntity<Map<String, String>> enviarCobranca(@PathVariable Long id) {
        boletoService.enviarCobrancaBoleto(id);
        return ResponseEntity.ok(Map.of("message", "E-mail de cobrança enviado com sucesso!"));
    }

    @Operation(summary = "Registrar download de boleto")
    @PostMapping("/{id}/registrar-download")
    @PreAuthorize("hasAnyRole('SINDICO', 'MORADOR')")
    public ResponseEntity<Map<String, String>> registrarDownload(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        boletoService.registrarDownloadBoleto(id, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "Download registrado com sucesso!"));
    }
}