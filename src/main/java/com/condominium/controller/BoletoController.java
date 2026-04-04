package com.condominium.controller;

import com.condominium.dto.BoletoRequest;
import com.condominium.dto.BoletoResponse;
import com.condominium.service.BoletoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boletos")
@CrossOrigin(origins = "*")
public class BoletoController {

    @Autowired
    private BoletoService boletoService;

    @GetMapping
    public ResponseEntity<List<BoletoResponse>> listarTodos() {
        return ResponseEntity.ok(boletoService.listarBoletos());
    }

    @GetMapping("/morador/{moradorId}")
    public ResponseEntity<List<BoletoResponse>> listarPorMorador(@PathVariable Long moradorId) {
        return ResponseEntity.ok(boletoService.listarBoletosPorMorador(moradorId));
    }

    @PostMapping
    public ResponseEntity<BoletoResponse> criar(@RequestBody BoletoRequest request) {
        return ResponseEntity.ok(boletoService.gerarBoleto(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoletoResponse> atualizar(@PathVariable Long id, @RequestBody BoletoRequest request) {
        return ResponseEntity.ok(boletoService.atualizarBoleto(id, request));
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<BoletoResponse> pagar(@PathVariable Long id) {
        return ResponseEntity.ok(boletoService.pagarBoleto(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        boletoService.deletarBoleto(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enviar-email")
    public ResponseEntity<Map<String, String>> enviarEmail(@PathVariable Long id) {
        boletoService.enviarEmailBoleto(id);
        return ResponseEntity.ok(Map.of("message", "E-mail enviado com sucesso!"));
    }
}
