package com.condominium.service.impl;

import com.condominium.dto.UserDTO;
import com.condominium.exception.BusinessException;
import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.Usuario;
import com.condominium.repository.BoletoRepository;
import com.condominium.repository.OcorrenciaRepository;
import com.condominium.repository.ReservaRepository;
import com.condominium.repository.UsuarioRepository;
import com.condominium.service.EmailService;
import com.condominium.service.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private OcorrenciaRepository ocorrenciaRepository;
    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private BoletoRepository boletoRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl service;

    private Usuario morador;

    @BeforeEach
    void setUp() {
        morador = new Usuario();
        morador.setId(1L);
        morador.setNome("João");
        morador.setEmail("joao@email.com");
        morador.setSenha("hashedpwd");
        morador.setRole(Usuario.Role.MORADOR);
        morador.setAtivo(true);
        morador.setApartamento("101");
        morador.setBloco("A");
        morador.setTelefone("99999-9999");
        morador.setCpf("000.000.000-00");
    }

    // listarTodos

    @Test
    void listarTodos_retornaListaDeMoradores() {
        when(usuarioRepository.findAll()).thenReturn(List.of(morador));
        List<UserDTO> result = service.listarTodos();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).nome()).isEqualTo("João");
    }

    // criar

    @Test
    void criar_emailDisponivel_conviteEnviado_retornaMapComConviteTrue() {
        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(usuarioRepository.save(any())).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });
        doNothing().when(emailService).enviarConvite(any(), any(), any(), any(), any());

        Map<String, Object> result = service.criar(morador);

        assertThat(result.get("conviteEnviado")).isEqualTo(true);
        assertThat(result.get("message").toString()).contains("convite enviado");
    }

    @Test
    void criar_emailDisponivel_conviteFalha_retornaMapComConviteFalse() {
        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(usuarioRepository.save(any())).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });
        doThrow(new RuntimeException("SMTP error"))
                .when(emailService).enviarConvite(any(), any(), any(), any(), any());

        Map<String, Object> result = service.criar(morador);

        assertThat(result.get("conviteEnviado")).isEqualTo(false);
        assertThat(result.get("message").toString()).contains("falha");
    }

    @Test
    void criar_emailEmUso_lancaBusinessException() {
        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(true);
        assertThatThrownBy(() -> service.criar(morador))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("E-mail");
    }

    // reenviarConvite

    @Test
    void reenviarConvite_encontrado_conviteEnviado_retornaSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(usuarioRepository.save(any())).thenReturn(morador);
        doNothing().when(emailService).enviarConvite(any(), any(), any(), any(), any());

        Map<String, Object> result = service.reenviarConvite(1L);
        assertThat(result.get("success")).isEqualTo(true);
    }

    @Test
    void reenviarConvite_encontrado_emailFalha_retornaFalha() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(usuarioRepository.save(any())).thenReturn(morador);
        doThrow(new RuntimeException("Timeout"))
                .when(emailService).enviarConvite(any(), any(), any(), any(), any());

        Map<String, Object> result = service.reenviarConvite(1L);
        assertThat(result.get("success")).isEqualTo(false);
        assertThat(result.get("message").toString()).contains("Falha");
    }

    @Test
    void reenviarConvite_naoEncontrado_lancaResourceNotFoundException() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.reenviarConvite(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // atualizar

    @Test
    void atualizar_encontrado_comSenha_atualizaTodosOsCampos() {
        var atualizado = new Usuario();
        atualizado.setNome("João Silva");
        atualizado.setEmail("joaosilva@email.com");
        atualizado.setCpf("111.111.111-11");
        atualizado.setTelefone("88888-8888");
        atualizado.setApartamento("202");
        atualizado.setBloco("B");
        atualizado.setSenha("nova-senha");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(passwordEncoder.encode("nova-senha")).thenReturn("nova-hash");
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserDTO result = service.atualizar(1L, atualizado);
        assertThat(result.nome()).isEqualTo("João Silva");
        verify(passwordEncoder).encode("nova-senha");
    }

    @Test
    void atualizar_encontrado_senhaEmBranco_naoMudaSenha() {
        var atualizado = new Usuario();
        atualizado.setNome("João");
        atualizado.setEmail("joao@email.com");
        atualizado.setCpf("000.000.000-00");
        atualizado.setTelefone("99999-9999");
        atualizado.setApartamento("101");
        atualizado.setBloco("A");
        atualizado.setSenha("   ");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.atualizar(1L, atualizado);
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void atualizar_encontrado_senhaNula_naoMudaSenha() {
        var atualizado = new Usuario();
        atualizado.setNome("João");
        atualizado.setEmail("joao@email.com");
        atualizado.setCpf("000.000.000-00");
        atualizado.setTelefone("99999-9999");
        atualizado.setApartamento("101");
        atualizado.setBloco("A");
        atualizado.setSenha(null);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.atualizar(1L, atualizado);
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void atualizar_naoEncontrado_lancaResourceNotFoundException() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.atualizar(99L, new Usuario()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // remover

    @Test
    void remover_encontrado_deletaMoradorERegistros() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(morador));
        Map<String, Object> result = service.remover(1L);
        verify(ocorrenciaRepository).deleteByMoradorId(1L);
        verify(reservaRepository).deleteByMoradorId(1L);
        verify(boletoRepository).deleteByMoradorId(1L);
        verify(usuarioRepository).delete(morador);
        assertThat(result.get("message").toString()).contains("sucesso");
    }

    @Test
    void remover_naoEncontrado_lancaResourceNotFoundException() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.remover(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // alternarStatus

    @Test
    void alternarStatus_ativoParaInativo() {
        morador.setAtivo(true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserDTO result = service.alternarStatus(1L);
        assertThat(result.ativo()).isFalse();
    }

    @Test
    void alternarStatus_inativoParaAtivo() {
        morador.setAtivo(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserDTO result = service.alternarStatus(1L);
        assertThat(result.ativo()).isTrue();
    }

    @Test
    void alternarStatus_naoEncontrado_lancaResourceNotFoundException() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.alternarStatus(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // alterarRole

    @Test
    void alterarRole_promocaoParaSindicoComSenhaValida_atualizaRole() {
        Usuario sindicoAtual = new Usuario();
        sindicoAtual.setId(2L);
        sindicoAtual.setNome("Maria");
        sindicoAtual.setEmail("maria@email.com");
        sindicoAtual.setSenha("senha-hash");
        sindicoAtual.setRole(Usuario.Role.SINDICO);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(usuarioRepository.findByEmail("maria@email.com")).thenReturn(Optional.of(sindicoAtual));
        when(passwordEncoder.matches("123456", "senha-hash")).thenReturn(true);
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserDTO result = service.alterarRole(1L, "SINDICO", "123456", "maria@email.com");
        assertThat(result.role()).isEqualTo("SINDICO");
    }

    @Test
    void alterarRole_roleInvalida_lancaBusinessException() {
        assertThatThrownBy(() -> service.alterarRole(1L, "INVALIDA", null, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Role inválida");
    }

    @Test
    void alterarRole_roleNula_lancaBusinessException() {
        assertThatThrownBy(() -> service.alterarRole(1L, null, null, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("obrigatório");
    }

    @Test
    void alterarRole_roleEmBranco_lancaBusinessException() {
        assertThatThrownBy(() -> service.alterarRole(1L, "  ", null, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("obrigatório");
    }

    @Test
    void alterarRole_naoEncontrado_lancaResourceNotFoundException() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.alterarRole(99L, "MORADOR", null, null))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void alterarRole_promocaoParaSindicoSemSenha_lancaBusinessException() {
        assertThatThrownBy(() -> service.alterarRole(1L, "SINDICO", null, "maria@email.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Digite sua senha");
    }
}
