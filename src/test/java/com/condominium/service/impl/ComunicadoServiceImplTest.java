package com.condominium.service.impl;

import com.condominium.dto.ComunicadoDTO;
import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.Comunicado;
import com.condominium.repository.ComunicadoRepository;
import com.condominium.service.ComunicadoServiceImpl;
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
class ComunicadoServiceImplTest {

    @Mock
    private ComunicadoRepository comunicadoRepository;

    @InjectMocks
    private ComunicadoServiceImpl service;

    private Comunicado comunicado;

    @BeforeEach
    void setUp() {
        comunicado = new Comunicado();
        comunicado.setId(1L);
        comunicado.setTitulo("Aviso importante");
        comunicado.setConteudo("Conteúdo do aviso");
        comunicado.setAutor("Síndico");
        comunicado.setDataCriacao(LocalDateTime.of(2026, 4, 10, 8, 0));
        comunicado.setImportante(true);
        comunicado.setCategoria("GERAL");
    }

    // listarTodos

    @Test
    void listarTodos_retornaListaMapeada() {
        when(comunicadoRepository.findAllByOrderByDataCriacaoDesc()).thenReturn(List.of(comunicado));
        List<ComunicadoDTO> result = service.listarTodos();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).titulo()).isEqualTo("Aviso importante");
    }

    // criar

    @Test
    void criar_comAutorJaDefinido_mantemAutor() {
        var novo = new Comunicado();
        novo.setTitulo("Novo");
        novo.setConteudo("Conteúdo");
        novo.setAutor("Maria");
        novo.setCategoria("AVISO");
        novo.setDataCriacao(LocalDateTime.now());

        when(comunicadoRepository.save(any())).thenAnswer(inv -> {
            Comunicado c = inv.getArgument(0);
            c.setId(2L);
            return c;
        });

        ComunicadoDTO result = service.criar(novo);
        assertThat(result.autor()).isEqualTo("Maria");
    }

    @Test
    void criar_comAutorNulo_defineAdministracao() {
        var novo = new Comunicado();
        novo.setTitulo("Novo");
        novo.setConteudo("Conteúdo");
        novo.setAutor(null);
        novo.setCategoria("AVISO");

        when(comunicadoRepository.save(any())).thenAnswer(inv -> {
            Comunicado c = inv.getArgument(0);
            c.setId(3L);
            c.setDataCriacao(LocalDateTime.now());
            return c;
        });

        ComunicadoDTO result = service.criar(novo);
        assertThat(result.autor()).isEqualTo("Administração");
    }

    // atualizar

    @Test
    void atualizar_encontrado_comAutor_atualizaTodosOsCampos() {
        var atualizado = new Comunicado();
        atualizado.setTitulo("Título novo");
        atualizado.setConteudo("Conteúdo novo");
        atualizado.setCategoria("URGENTE");
        atualizado.setImportante(false);
        atualizado.setAutor("Novo Autor");

        when(comunicadoRepository.findById(1L)).thenReturn(Optional.of(comunicado));
        when(comunicadoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ComunicadoDTO result = service.atualizar(1L, atualizado);
        assertThat(result.titulo()).isEqualTo("Título novo");
        assertThat(result.autor()).isEqualTo("Novo Autor");
        assertThat(result.importante()).isFalse();
    }

    @Test
    void atualizar_encontrado_semAutor_mantemAutorExistente() {
        var atualizado = new Comunicado();
        atualizado.setTitulo("Atualizado");
        atualizado.setConteudo("Conteúdo");
        atualizado.setCategoria("GERAL");
        atualizado.setImportante(true);
        atualizado.setAutor(null);

        when(comunicadoRepository.findById(1L)).thenReturn(Optional.of(comunicado));
        when(comunicadoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ComunicadoDTO result = service.atualizar(1L, atualizado);
        assertThat(result.autor()).isEqualTo("Síndico");
    }

    @Test
    void atualizar_naoEncontrado_lancaResourceNotFoundException() {
        when(comunicadoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.atualizar(99L, new Comunicado()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // remover

    @Test
    void remover_existente_deletaComSucesso() {
        when(comunicadoRepository.existsById(1L)).thenReturn(true);
        service.remover(1L);
        verify(comunicadoRepository).deleteById(1L);
    }

    @Test
    void remover_naoExistente_lancaResourceNotFoundException() {
        when(comunicadoRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> service.remover(99L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(comunicadoRepository, never()).deleteById(any());
    }
}
