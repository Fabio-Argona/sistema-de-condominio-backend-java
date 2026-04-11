package com.condominium.controller;

import com.condominium.dto.ReservaDTO;
import com.condominium.model.Reserva;
import com.condominium.service.impl.IReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Reservas de áreas comuns")
@SecurityRequirement(name = "bearerAuth")
public class ReservaController {

    private final IReservaService reservaService;

    public ReservaController(IReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @Operation(summary = "Listar todas as reservas")
    @GetMapping
    @PreAuthorize("hasRole('SINDICO')")
    public List<ReservaDTO> listarTodas() {
        return reservaService.listarTodas();
    }

    @Operation(summary = "Listar reservas de um morador")
    @GetMapping("/morador/{moradorId}")
    @PreAuthorize("hasAnyRole('SINDICO', 'MORADOR')")
    public List<ReservaDTO> listarPorMorador(@PathVariable Long moradorId) {
        return reservaService.listarPorMorador(moradorId);
    }

    @Operation(summary = "Criar reserva", description = "Retorna 409 se houver conflito de horário na área comum.")
    @PostMapping("/morador/{moradorId}/area/{areaId}")
    @PreAuthorize("hasAnyRole('SINDICO', 'MORADOR')")
    public ResponseEntity<?> criar(
            @PathVariable Long moradorId,
            @PathVariable Long areaId,
            @RequestBody Reserva reserva) {
        Object resultado = reservaService.criar(moradorId, areaId, reserva);
        if (resultado instanceof ResponseEntity) {
            return (ResponseEntity<?>) resultado;
        }
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Atualizar status da reserva (APROVADA / REJEITADA / CANCELADA)")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SINDICO')")
    public ResponseEntity<ReservaDTO> atualizarStatus(@PathVariable Long id, @RequestBody Reserva dadosAtualizacao) {
        return ResponseEntity.ok(reservaService.atualizarStatus(id, dadosAtualizacao));
    }
}