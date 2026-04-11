package com.condominium.service.impl;

import com.condominium.dto.FornecedorDTO;
import com.condominium.exception.ForbiddenException;
import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.Fornecedor;
import com.condominium.repository.FornecedorRepository;
import com.condominium.service.FornecedorServiceImpl;
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
class FornecedorServiceImplTest {

    @Mock
    private FornecedorRepository fornecedorRepository;

    @InjectMocks
    private FornecedorServiceImpl service;

    private Fornecedor fornecedor;

    @BeforeEach
    void setUp() {
        fornecedor = new Fornecedor();
        fornecedor.setId(10L);
        fornecedor.setNome("Empresa X");
        fornecedor.setMoradorId(5L);
        fornecedor.setComentario("Ótimo serviço");
        fornecedor.setVigencia("2026-12-31");
        fornecedor.setContato("99999-9999");
        fornecedor.setValor("R$ 500,00");
        fornecedor.setDataCriacao(LocalDateTime.now());
        fornecedor.setDataAtualizacao(LocalDateTime.now());
    }

    // listarTodos

    @Test
    void listarTodos_retornaListaMapeada() {
        when(fornecedorRepository.findAllByOrderByNomeAsc()).thenReturn(List.of(fornecedor));
        List<FornecedorDTO> result = service.listarTodos();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).nome()).isEqualTo("Empresa X");
    }

    // listarPorMorador

    @Test
    void listarPorMorador_retornaListaFiltrada() {
        when(fornecedorRepository.findByMoradorIdOrderByNomeAsc(5L)).thenReturn(List.of(fornecedor));
        List<FornecedorDTO> result = service.listarPorMorador(5L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).moradorId()).isEqualTo(5L);
    }

    // criar

    @Test
    void criar_salvaNovo() {
        when(fornecedorRepository.save(any())).thenReturn(fornecedor);
        FornecedorDTO result = service.criar(fornecedor);
        assertThat(result.nome()).isEqualTo("Empresa X");
    }

    // criarParaMorador

    @Test
    void criarParaMorador_associaMoradorIdESalva() {
        var novo = new Fornecedor();
        novo.setNome("Novo");
        novo.setDataCriacao(LocalDateTime.now());
        novo.setDataAtualizacao(LocalDateTime.now());

        when(fornecedorRepository.save(any())).thenAnswer(inv -> {
            Fornecedor f = inv.getArgument(0);
            f.setId(99L);
            return f;
        });

        FornecedorDTO result = service.criarParaMorador(7L, novo);
        assertThat(result.moradorId()).isEqualTo(7L);
    }

    // atualizar

    @Test
    void atualizar_encontrado_atualizaCampos() {
        var dados = new Fornecedor();
        dados.setNome("Empresa Y");
        dados.setComentario("Bom serviço");
        dados.setVigencia("2027-01-01");
        dados.setContato("88888-8888");
        dados.setValor("R$ 600,00");

        when(fornecedorRepository.findById(10L)).thenReturn(Optional.of(fornecedor));
        when(fornecedorRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FornecedorDTO result = service.atualizar(10L, dados);
        assertThat(result.nome()).isEqualTo("Empresa Y");
        assertThat(result.contato()).isEqualTo("88888-8888");
    }

    @Test
    void atualizar_naoEncontrado_lancaResourceNotFoundException() {
        when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.atualizar(99L, new Fornecedor()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // atualizarParaMorador

    @Test
    void atualizarParaMorador_proprietario_atualizaCampos() {
        var dados = new Fornecedor();
        dados.setNome("Atualizado");
        dados.setComentario("Atualizado");
        dados.setVigencia("2027-01-01");
        dados.setContato("77777-7777");
        dados.setValor("R$ 750,00");

        when(fornecedorRepository.findById(10L)).thenReturn(Optional.of(fornecedor));
        when(fornecedorRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FornecedorDTO result = service.atualizarParaMorador(5L, 10L, dados);
        assertThat(result.nome()).isEqualTo("Atualizado");
    }

    @Test
    void atualizarParaMorador_naoProprietario_lancaForbiddenException() {
        when(fornecedorRepository.findById(10L)).thenReturn(Optional.of(fornecedor));
        assertThatThrownBy(() -> service.atualizarParaMorador(99L, 10L, new Fornecedor()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void atualizarParaMorador_naoEncontrado_lancaResourceNotFoundException() {
        when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.atualizarParaMorador(5L, 99L, new Fornecedor()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // deletar

    @Test
    void deletar_existente_deletaComSucesso() {
        when(fornecedorRepository.existsById(10L)).thenReturn(true);
        service.deletar(10L);
        verify(fornecedorRepository).deleteById(10L);
    }

    @Test
    void deletar_naoExistente_lancaResourceNotFoundException() {
        when(fornecedorRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> service.deletar(99L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(fornecedorRepository, never()).deleteById(any());
    }

    // deletarParaMorador

    @Test
    void deletarParaMorador_proprietario_deletaComSucesso() {
        when(fornecedorRepository.findById(10L)).thenReturn(Optional.of(fornecedor));
        service.deletarParaMorador(5L, 10L);
        verify(fornecedorRepository).deleteById(10L);
    }

    @Test
    void deletarParaMorador_naoProprietario_lancaForbiddenException() {
        when(fornecedorRepository.findById(10L)).thenReturn(Optional.of(fornecedor));
        assertThatThrownBy(() -> service.deletarParaMorador(99L, 10L))
                .isInstanceOf(ForbiddenException.class);
        verify(fornecedorRepository, never()).deleteById(any());
    }

    @Test
    void deletarParaMorador_naoEncontrado_lancaResourceNotFoundException() {
        when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.deletarParaMorador(5L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
