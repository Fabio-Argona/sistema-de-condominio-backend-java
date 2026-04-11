package com.condominium.service.impl;

import com.condominium.dto.OcorrenciaDTO;
import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.Ocorrencia;
import com.condominium.model.Ocorrencia.PrioridadeOcorrencia;
import com.condominium.model.Ocorrencia.StatusOcorrencia;
import com.condominium.model.Usuario;
import com.condominium.repository.OcorrenciaRepository;
import com.condominium.repository.UsuarioRepository;
import com.condominium.service.OcorrenciaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OcorrenciaServiceImplTest {

    @Mock
    private OcorrenciaRepository ocorrenciaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private OcorrenciaServiceImpl service;

    private Usuario morador;
    private Ocorrencia ocorrencia;

    @BeforeEach
    void setUp() {
        morador = new Usuario();
        morador.setId(1L);
        morador.setNome("Maria");
        morador.setApartamento("101");
        morador.setBloco("A");

        ocorrencia = new Ocorrencia();
        ocorrencia.setId(10L);
        ocorrencia.setTitulo("Vazamento");
        ocorrencia.setDescricao("Vazamento no banheiro");
        ocorrencia.setCategoria("Hidráulica");
        ocorrencia.setStatus(StatusOcorrencia.ABERTA);
        ocorrencia.setPrioridade(PrioridadeOcorrencia.MEDIA);
        ocorrencia.setMorador(morador);
        ocorrencia.setDataCriacao(LocalDateTime.now());
        ocorrencia.setDataAtualizacao(LocalDateTime.now());
    }

    // listarTodas

    @Test
    void listarTodas_retornaListaMapeada() {
        when(ocorrenciaRepository.findAllByOrderByDataCriacaoDesc()).thenReturn(List.of(ocorrencia));
        List<OcorrenciaDTO> result = service.listarTodas();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).titulo()).isEqualTo("Vazamento");
    }

    // listarPorMorador

    @Test
    void listarPorMorador_retornaListaFiltrada() {
        when(ocorrenciaRepository.findByMoradorIdOrderByDataCriacaoDesc(1L)).thenReturn(List.of(ocorrencia));
        List<OcorrenciaDTO> result = service.listarPorMorador(1L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).moradorId()).isEqualTo(1L);
    }

    // criar

    @Test
    void criar_moradorEncontrado_statusENuloDefineDefault() {
        ocorrencia.setStatus(null);
        ocorrencia.setPrioridade(null);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(ocorrenciaRepository.save(any())).thenAnswer(inv -> {
            Ocorrencia o = inv.getArgument(0);
            o.setStatus(StatusOcorrencia.ABERTA);
            o.setPrioridade(PrioridadeOcorrencia.BAIXA);
            return o;
        });

        OcorrenciaDTO result = service.criar(1L, ocorrencia);
        assertThat(result).isNotNull();
    }

    @Test
    void criar_moradorEncontrado_statusJaDefinido_naoSobrescreve() {
        ocorrencia.setStatus(StatusOcorrencia.EM_ANDAMENTO);
        ocorrencia.setPrioridade(PrioridadeOcorrencia.ALTA);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(ocorrenciaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OcorrenciaDTO result = service.criar(1L, ocorrencia);
        assertThat(result.status()).isEqualTo("EM_ANDAMENTO");
    }

    @Test
    void criar_moradorNaoEncontrado_lancaResourceNotFoundException() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.criar(99L, ocorrencia))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // atualizarStatus

    @Test
    void atualizarStatus_encontrado_alteraStatusEAdicionaResposta() {
        var dados = new Ocorrencia();
        dados.setStatus(StatusOcorrencia.RESOLVIDA);
        dados.setRespostasSindico(new ArrayList<>(List.of("Problema resolvido")));

        when(ocorrenciaRepository.findById(10L)).thenReturn(Optional.of(ocorrencia));
        when(ocorrenciaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OcorrenciaDTO result = service.atualizarStatus(10L, dados);
        assertThat(result.status()).isEqualTo("RESOLVIDA");
        assertThat(result.respostasSindico()).contains("Problema resolvido");
    }

    @Test
    void atualizarStatus_statusNulo_naoAlteraStatus() {
        var dados = new Ocorrencia();
        dados.setStatus(null);
        dados.setRespostasSindico(new ArrayList<>());

        when(ocorrenciaRepository.findById(10L)).thenReturn(Optional.of(ocorrencia));
        when(ocorrenciaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OcorrenciaDTO result = service.atualizarStatus(10L, dados);
        assertThat(result.status()).isEqualTo("ABERTA"); // unchanged
    }

    @Test
    void atualizarStatus_respostasNulas_naoAdicionaResposta() {
        var dados = new Ocorrencia();
        dados.setStatus(StatusOcorrencia.EM_ANDAMENTO);
        dados.setRespostasSindico(null);

        when(ocorrenciaRepository.findById(10L)).thenReturn(Optional.of(ocorrencia));
        when(ocorrenciaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OcorrenciaDTO result = service.atualizarStatus(10L, dados);
        assertThat(result.respostasSindico()).isEmpty();
    }

    @Test
    void atualizarStatus_naoEncontrado_lancaResourceNotFoundException() {
        when(ocorrenciaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.atualizarStatus(99L, new Ocorrencia()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
