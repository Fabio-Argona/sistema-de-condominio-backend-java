package com.condominium.controller;

import com.condominium.dto.ReservaDTO;
import com.condominium.model.Reserva;
import com.condominium.repository.AreaComumRepository;
import com.condominium.repository.ReservaRepository;
import com.condominium.repository.UsuarioRepository;
import com.condominium.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaRepository reservaRepository;
    private final AreaComumRepository areaComumRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    public ReservaController(ReservaRepository reservaRepository, AreaComumRepository areaComumRepository, UsuarioRepository usuarioRepository, EmailService emailService) {
        this.reservaRepository = reservaRepository;
        this.areaComumRepository = areaComumRepository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
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
    public ResponseEntity<?> criar(
            @PathVariable Long moradorId,
            @PathVariable Long areaId,
            @RequestBody Reserva reserva) {
        
        return usuarioRepository.findById(moradorId).flatMap(morador -> 
            areaComumRepository.findById(areaId).map(area -> {
                // Verificar sobreposição de horários
                List<Reserva> conflitos = reservaRepository.findSobreposicoes(
                    areaId, 
                    reserva.getDataReserva(), 
                    reserva.getHoraInicio(), 
                    reserva.getHoraFim()
                );

                if (!conflitos.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Já existe uma reserva confirmada ou pendente para esta área neste horário."));
                }

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
                
                // Enviar e-mail de resposta (Aprovada/Rejeitada) para o morador
                if (reserva.getMorador() != null && 
                   (reserva.getStatus() == Reserva.StatusReserva.APROVADA || 
                    reserva.getStatus() == Reserva.StatusReserva.REJEITADA)) {
                    
                    emailService.enviarEmailStatusReserva(
                        reserva.getMorador().getEmail(),
                        reserva.getAreaComum().getNome(),
                        reserva.getDataReserva().toString(),
                        reserva.getHoraInicio() + " - " + resortHoraFim(reserva),
                        reserva.getStatus().name()
                    );
                }
            }
            Reserva salva = reservaRepository.save(reserva);
            return ResponseEntity.ok(ReservaDTO.fromEntity(salva));
        }).orElse(ResponseEntity.notFound().build());
    }

    private String resortHoraFim(Reserva r) {
        return r.getHoraFim();
    }
}
