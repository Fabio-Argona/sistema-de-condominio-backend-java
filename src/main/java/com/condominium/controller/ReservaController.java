package com.condominium.controller;

import com.condominium.dto.ReservaDTO;
import com.condominium.model.Reserva;
import com.condominium.repository.AreaComumRepository;
import com.condominium.repository.ReservaRepository;
import com.condominium.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaRepository reservaRepository;
    private final AreaComumRepository areaComumRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaController(ReservaRepository reservaRepository, AreaComumRepository areaComumRepository, UsuarioRepository usuarioRepository) {
        this.reservaRepository = reservaRepository;
        this.areaComumRepository = areaComumRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<ReservaDTO> listarTodas() {
        return reservaRepository.findAllByOrderByDataReservaDescHoraInicioDesc().stream()
                .map(ReservaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/morador/{moradorId}")
    public List<ReservaDTO> listarPorMorador(@PathVariable Long moradorId) {
        return reservaRepository.findByMoradorIdOrderByDataReservaDescHoraInicioDesc(moradorId).stream()
                .map(ReservaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping("/morador/{moradorId}/area/{areaId}")
    public ResponseEntity<ReservaDTO> criar(
            @PathVariable Long moradorId,
            @PathVariable Long areaId,
            @RequestBody Reserva reserva) {
        
        return usuarioRepository.findById(moradorId).flatMap(morador -> 
            areaComumRepository.findById(areaId).map(area -> {
                reserva.setMorador(morador);
                reserva.setAreaComum(area);
                if (reserva.getStatus() == null) {
                    reserva.setStatus(Reserva.StatusReserva.PENDENTE);
                }
                
                Reserva salva = reservaRepository.save(reserva);
                return ResponseEntity.ok(ReservaDTO.fromEntity(salva));
            })
        ).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ReservaDTO> atualizarStatus(@PathVariable Long id, @RequestBody Reserva dadosAtualizacao) {
        return reservaRepository.findById(id).map(reserva -> {
            if (dadosAtualizacao.getStatus() != null) {
                reserva.setStatus(dadosAtualizacao.getStatus());
            }
            Reserva salva = reservaRepository.save(reserva);
            return ResponseEntity.ok(ReservaDTO.fromEntity(salva));
        }).orElse(ResponseEntity.notFound().build());
    }
}
