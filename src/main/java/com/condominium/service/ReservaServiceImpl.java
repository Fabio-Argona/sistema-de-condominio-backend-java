package com.condominium.service;

import com.condominium.dto.ReservaDTO;
import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.Reserva;
import com.condominium.repository.AreaComumRepository;
import com.condominium.repository.ReservaRepository;
import com.condominium.repository.UsuarioRepository;
import com.condominium.service.impl.IReservaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservaServiceImpl implements IReservaService {

    private final ReservaRepository reservaRepository;
    private final AreaComumRepository areaComumRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    public ReservaServiceImpl(ReservaRepository reservaRepository,
                               AreaComumRepository areaComumRepository,
                               UsuarioRepository usuarioRepository,
                               EmailService emailService) {
        this.reservaRepository = reservaRepository;
        this.areaComumRepository = areaComumRepository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
    }

    @Override
    public List<ReservaDTO> listarTodas() {
        return reservaRepository.findAllByOrderByDataReservaDescHoraInicioDesc().stream()
                .map(ReservaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservaDTO> listarPorMorador(Long moradorId) {
        return reservaRepository.findByMoradorIdOrderByDataReservaDescHoraInicioDesc(moradorId).stream()
                .map(ReservaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Object criar(Long moradorId, Long areaId, Reserva reserva) {
        var morador = usuarioRepository.findById(moradorId)
                .orElseThrow(() -> new ResourceNotFoundException("Morador", moradorId));
        var area = areaComumRepository.findById(areaId)
                .orElseThrow(() -> new ResourceNotFoundException("Área Comum", areaId));

        List<Reserva> conflitos = reservaRepository.findSobreposicoes(
                areaId, reserva.getDataReserva(), reserva.getHoraInicio(), reserva.getHoraFim());

        if (!conflitos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Já existe uma reserva confirmada ou pendente para esta área neste horário."));
        }

        reserva.setMorador(morador);
        reserva.setAreaComum(area);
        if (reserva.getStatus() == null) {
            reserva.setStatus(Reserva.StatusReserva.PENDENTE);
        }
        return ReservaDTO.fromEntity(reservaRepository.save(reserva));
    }

    @Override
    public ReservaDTO atualizarStatus(Long id, Reserva dadosAtualizacao) {
        var reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));
        if (dadosAtualizacao.getStatus() != null) {
            reserva.setStatus(dadosAtualizacao.getStatus());

            if (reserva.getMorador() != null &&
                    (reserva.getStatus() == Reserva.StatusReserva.APROVADA ||
                     reserva.getStatus() == Reserva.StatusReserva.REJEITADA)) {
                emailService.enviarEmailStatusReserva(
                        reserva.getMorador().getEmail(),
                        reserva.getAreaComum().getNome(),
                        reserva.getDataReserva().toString(),
                        reserva.getHoraInicio() + " - " + reserva.getHoraFim(),
                        reserva.getStatus().name());
            }
        }
        return ReservaDTO.fromEntity(reservaRepository.save(reserva));
    }
}
