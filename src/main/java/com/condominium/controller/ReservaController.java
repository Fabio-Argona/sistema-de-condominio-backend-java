package com.condominium.controller;

import com.condominium.dto.ReservaDTO;
import com.condominium.model.Reserva;
import com.condominium.service.IReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final IReservaService reservaService;

    public ReservaController(IReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public List<ReservaDTO> listarTodas() {
        return reservaService.listarTodas();
    }

    @GetMapping("/morador/{moradorId}")
    public List<ReservaDTO> listarPorMorador(@PathVariable Long moradorId) {
        return reservaService.listarPorMorador(moradorId);
    }

    @PostMapping("/morador/{moradorId}/area/{areaId}")
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

    @PatchMapping("/{id}/status")
    public ResponseEntity<ReservaDTO> atualizarStatus(@PathVariable Long id, @RequestBody Reserva dadosAtualizacao) {
        return ResponseEntity.ok(reservaService.atualizarStatus(id, dadosAtualizacao));
    }
}
