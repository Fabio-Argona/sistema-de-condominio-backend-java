package com.condominium.service.impl;

import com.condominium.dto.ReservaDTO;
import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.AreaComum;
import com.condominium.model.Reserva;
import com.condominium.model.Reserva.StatusReserva;
import com.condominium.model.Usuario;
import com.condominium.repository.AreaComumRepository;
import com.condominium.repository.ReservaRepository;
import com.condominium.repository.UsuarioRepository;
import com.condominium.service.EmailService;
import com.condominium.service.ReservaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private AreaComumRepository areaComumRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private ReservaServiceImpl service;

    private Usuario morador;
    private AreaComum area;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        morador = new Usuario();
        morador.setId(1L);
        morador.setNome("João");
        morador.setEmail("joao@email.com");
        morador.setApartamento("101");
        morador.setBloco("A");

        area = new AreaComum();
        area.setId(2L);
        area.setNome("Salão de Festas");

        reserva = new Reserva();
        reserva.setId(20L);
        reserva.setMorador(morador);
        reserva.setAreaComum(area);
        reserva.setDataReserva(LocalDate.of(2026, 5, 1));
        reserva.setHoraInicio("14:00");
        reserva.setHoraFim("18:00");
        reserva.setStatus(StatusReserva.PENDENTE);
        reserva.setDataCriacao(LocalDateTime.now());
    }

    // listarTodas

    @Test
    void listarTodas_retornaListaMapeada() {
        when(reservaRepository.findAllByOrderByDataReservaDescHoraInicioDesc()).thenReturn(List.of(reserva));
        List<ReservaDTO> result = service.listarTodas();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).areaComumNome()).isEqualTo("Salão de Festas");
    }

    // listarPorMorador

    @Test
    void listarPorMorador_retornaListaFiltrada() {
        when(reservaRepository.findByMoradorIdOrderByDataReservaDescHoraInicioDesc(1L)).thenReturn(List.of(reserva));
        List<ReservaDTO> result = service.listarPorMorador(1L);
        assertThat(result).hasSize(1);
    }

    // criar

    @Test
    void criar_moradorNaoEncontrado_lancaResourceNotFoundException() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.criar(99L, 2L, new Reserva()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void criar_areaNaoEncontrada_lancaResourceNotFoundException() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(areaComumRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.criar(1L, 99L, new Reserva()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void criar_comConflito_retornaResponseEntityConflito() {
        var req = new Reserva();
        req.setDataReserva(LocalDate.of(2026, 5, 1));
        req.setHoraInicio("14:00");
        req.setHoraFim("18:00");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(areaComumRepository.findById(2L)).thenReturn(Optional.of(area));
        when(reservaRepository.findSobreposicoes(any(), any(), any(), any())).thenReturn(List.of(reserva));

        Object result = service.criar(1L, 2L, req);
        assertThat(result).isInstanceOf(ResponseEntity.class);
        assertThat(((ResponseEntity<?>) result).getStatusCode().value()).isEqualTo(409);
    }

    @Test
    void criar_semConflito_statusNulo_defineComoPendente() {
        var req = new Reserva();
        req.setDataReserva(LocalDate.of(2026, 6, 1));
        req.setHoraInicio("10:00");
        req.setHoraFim("12:00");
        req.setStatus(null);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(areaComumRepository.findById(2L)).thenReturn(Optional.of(area));
        when(reservaRepository.findSobreposicoes(any(), any(), any(), any())).thenReturn(List.of());
        when(reservaRepository.save(any())).thenAnswer(inv -> {
            Reserva r = inv.getArgument(0);
            r.setDataCriacao(LocalDateTime.now());
            return r;
        });

        Object result = service.criar(1L, 2L, req);
        assertThat(result).isInstanceOf(ReservaDTO.class);
        assertThat(((ReservaDTO) result).status()).isEqualTo("PENDENTE");
    }

    @Test
    void criar_semConflito_statusJaDefinido_mantemStatus() {
        var req = new Reserva();
        req.setDataReserva(LocalDate.of(2026, 6, 1));
        req.setHoraInicio("10:00");
        req.setHoraFim("12:00");
        req.setStatus(StatusReserva.APROVADA);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(areaComumRepository.findById(2L)).thenReturn(Optional.of(area));
        when(reservaRepository.findSobreposicoes(any(), any(), any(), any())).thenReturn(List.of());
        when(reservaRepository.save(any())).thenAnswer(inv -> {
            Reserva r = inv.getArgument(0);
            r.setDataCriacao(LocalDateTime.now());
            return r;
        });

        Object result = service.criar(1L, 2L, req);
        assertThat(((ReservaDTO) result).status()).isEqualTo("APROVADA");
    }

    // atualizarStatus

    @Test
    void atualizarStatus_aprovada_enviaNEmailAoMorador() {
        var dados = new Reserva();
        dados.setStatus(StatusReserva.APROVADA);

        when(reservaRepository.findById(20L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReservaDTO result = service.atualizarStatus(20L, dados);
        assertThat(result.status()).isEqualTo("APROVADA");
        verify(emailService).enviarEmailStatusReserva(
                eq(morador.getEmail()), eq(area.getNome()), any(), any(), eq("APROVADA"));
    }

    @Test
    void atualizarStatus_rejeitada_enviaNEmailAoMorador() {
        var dados = new Reserva();
        dados.setStatus(StatusReserva.REJEITADA);

        when(reservaRepository.findById(20L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReservaDTO result = service.atualizarStatus(20L, dados);
        assertThat(result.status()).isEqualTo("REJEITADA");
        verify(emailService).enviarEmailStatusReserva(any(), any(), any(), any(), eq("REJEITADA"));
    }

    @Test
    void atualizarStatus_cancelada_naoEnviaNEmail() {
        var dados = new Reserva();
        dados.setStatus(StatusReserva.CANCELADA);

        when(reservaRepository.findById(20L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.atualizarStatus(20L, dados);
        verify(emailService, never()).enviarEmailStatusReserva(any(), any(), any(), any(), any());
    }

    @Test
    void atualizarStatus_statusNulo_naoAltera() {
        var dados = new Reserva();
        dados.setStatus(null);

        when(reservaRepository.findById(20L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReservaDTO result = service.atualizarStatus(20L, dados);
        assertThat(result.status()).isEqualTo("PENDENTE");
        verify(emailService, never()).enviarEmailStatusReserva(any(), any(), any(), any(), any());
    }

    @Test
    void atualizarStatus_naoEncontrado_lancaResourceNotFoundException() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.atualizarStatus(99L, new Reserva()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
