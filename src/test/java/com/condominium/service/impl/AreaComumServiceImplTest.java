package com.condominium.service.impl;

import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.AreaComum;
import com.condominium.repository.AreaComumRepository;
import com.condominium.service.AreaComumServiceImpl;
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
class AreaComumServiceImplTest {

    @Mock
    private AreaComumRepository areaComumRepository;

    @InjectMocks
    private AreaComumServiceImpl service;

    private AreaComum area;

    @BeforeEach
    void setUp() {
        area = new AreaComum();
        area.setId(1L);
        area.setNome("Salão de Festas");
        area.setDescricao("Espaço amplo para eventos");
        area.setCapacidade(50);
        area.setValorReserva(200.0);
        area.setHorarioAbertura("08:00");
        area.setHorarioFechamento("22:00");
        area.setDisponivel(true);
        area.setRegras("Sem barulho após as 22h");
    }

    // listarTodas

    @Test
    void listarTodas_retornaLista() {
        when(areaComumRepository.findAll()).thenReturn(List.of(area));
        List<AreaComum> result = service.listarTodas();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("Salão de Festas");
    }

    // criar

    @Test
    void criar_salvaNovo() {
        when(areaComumRepository.save(any())).thenReturn(area);
        AreaComum result = service.criar(area);
        assertThat(result.getId()).isEqualTo(1L);
    }

    // atualizar

    @Test
    void atualizar_encontrado_atualizaTodosOsCampos() {
        var dados = new AreaComum();
        dados.setNome("Quadra");
        dados.setDescricao("Quadra poliesportiva");
        dados.setCapacidade(30);
        dados.setValorReserva(100.0);
        dados.setHorarioAbertura("07:00");
        dados.setHorarioFechamento("21:00");
        dados.setDisponivel(false);
        dados.setRegras("Trazer equipamento próprio");

        when(areaComumRepository.findById(1L)).thenReturn(Optional.of(area));
        when(areaComumRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AreaComum result = service.atualizar(1L, dados);
        assertThat(result.getNome()).isEqualTo("Quadra");
        assertThat(result.getCapacidade()).isEqualTo(30);
        assertThat(result.getDisponivel()).isFalse();
    }

    @Test
    void atualizar_naoEncontrado_lancaResourceNotFoundException() {
        when(areaComumRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.atualizar(99L, new AreaComum()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // deletar

    @Test
    void deletar_existente_deletaComSucesso() {
        when(areaComumRepository.existsById(1L)).thenReturn(true);
        service.deletar(1L);
        verify(areaComumRepository).deleteById(1L);
    }

    @Test
    void deletar_naoExistente_lancaResourceNotFoundException() {
        when(areaComumRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> service.deletar(99L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(areaComumRepository, never()).deleteById(any());
    }
}
