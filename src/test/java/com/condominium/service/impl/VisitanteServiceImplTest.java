package com.condominium.service.impl;

import com.condominium.dto.VisitanteDTO;
import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.Visitante;
import com.condominium.repository.VisitanteRepository;
import com.condominium.service.VisitanteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitanteServiceImplTest {

    @Mock
    private VisitanteRepository visitanteRepository;

    @InjectMocks
    private VisitanteServiceImpl service;

    private Visitante visitante;

    @BeforeEach
    void setUp() {
        visitante = new Visitante();
        visitante.setId(1L);
        visitante.setNome("Carlos");
        visitante.setDocumento("123.456.789-00");
        visitante.setTelefone("99999-9999");
        visitante.setApartamento("202");
        visitante.setBloco("B");
        visitante.setMoradorNome("Ana");
        visitante.setDataEntrada(LocalDateTime.of(2026, 4, 10, 9, 0));
        visitante.setPorteiroNome("José");
    }

    // listar

    @Test
    void listar_retornaListaMapeada() {
        when(visitanteRepository.findAllByOrderByDataEntradaDesc()).thenReturn(List.of(visitante));
        List<VisitanteDTO> result = service.listar();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).nome()).isEqualTo("Carlos");
    }

    // criar

    @Test
    void criar_defineDataEntradaESalva() {
        var novo = new Visitante();
        novo.setNome("Pedro");
        novo.setDataEntrada(null);

        when(visitanteRepository.save(any())).thenAnswer(inv -> {
            Visitante v = inv.getArgument(0);
            v.setId(2L);
            return v;
        });

        VisitanteDTO result = service.criar(novo);
        assertThat(result).isNotNull();
        assertThat(novo.getDataEntrada()).isNotNull();
    }

    // registrarSaida

    @Test
    void registrarSaida_encontrado_defineDateSaida() {
        when(visitanteRepository.findById(1L)).thenReturn(Optional.of(visitante));
        when(visitanteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        VisitanteDTO result = service.registrarSaida(1L);
        assertThat(result).isNotNull();
        assertThat(visitante.getDataSaida()).isNotNull();
    }

    @Test
    void registrarSaida_naoEncontrado_lancaResourceNotFoundException() {
        when(visitanteRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.registrarSaida(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
