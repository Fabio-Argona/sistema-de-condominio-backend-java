package com.condominium.service.impl;

import com.condominium.dto.UnidadeDTO;
import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.Unidade;
import com.condominium.repository.UnidadeRepository;
import com.condominium.service.UnidadeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnidadeServiceImplTest {

    @Mock
    private UnidadeRepository unidadeRepository;

    @InjectMocks
    private UnidadeServiceImpl service;

    private Unidade unidade;

    @BeforeEach
    void setUp() {
        unidade = new Unidade();
        unidade.setId(1L);
        unidade.setNumero("101");
        unidade.setBloco("A");
        unidade.setStatus(Unidade.StatusUnidade.DISPONIVEL);
    }

    // listarTodas

    @Test
    void listarTodas_retornaListaMapeada() {
        when(unidadeRepository.findAll()).thenReturn(List.of(unidade));
        List<UnidadeDTO> result = service.listarTodas();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).numero()).isEqualTo("101");
        assertThat(result.get(0).status()).isEqualTo("DISPONIVEL");
    }

    // criar

    @Test
    void criar_salvaNovo() {
        when(unidadeRepository.save(any())).thenReturn(unidade);
        UnidadeDTO result = service.criar(unidade);
        assertThat(result.bloco()).isEqualTo("A");
    }

    // buscarPorId

    @Test
    void buscarPorId_encontrado_retornaDTO() {
        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidade));
        UnidadeDTO result = service.buscarPorId(1L);
        assertThat(result.numero()).isEqualTo("101");
    }

    @Test
    void buscarPorId_naoEncontrado_lancaResourceNotFoundException() {
        when(unidadeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
