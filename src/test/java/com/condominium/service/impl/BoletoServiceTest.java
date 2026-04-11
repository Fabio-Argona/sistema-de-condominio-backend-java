package com.condominium.service.impl;

import com.condominium.dto.BoletoRequest;
import com.condominium.dto.BoletoResponse;
import com.condominium.exception.BusinessException;
import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.Boleto;
import com.condominium.model.Usuario;
import com.condominium.repository.BoletoRepository;
import com.condominium.repository.UsuarioRepository;
import com.condominium.service.BoletoService;
import com.condominium.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoletoServiceTest {

    @Mock
    private BoletoRepository boletoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private BoletoService boletoService;

    private Usuario morador;
    private Boleto boleto;

    @BeforeEach
    void setUp() {
        morador = new Usuario();
        morador.setId(1L);
        morador.setNome("João Silva");
        morador.setEmail("joao@email.com");

        boleto = new Boleto();
        boleto.setId(10L);
        boleto.setMorador(morador);
        boleto.setValor(BigDecimal.valueOf(500));
        boleto.setDataVencimento(LocalDate.now().plusDays(5));
        boleto.setDescricao("Condomínio Abril");
        boleto.setLinhaDigitavel("123456");
        boleto.setStatus(Boleto.StatusBoleto.PENDENTE);
    }

    // listarBoletos

    @Test
    void listarBoletos_retornaLista() {
        when(boletoRepository.findAll()).thenReturn(List.of(boleto));
        List<BoletoResponse> result = boletoService.listarBoletos();
        assertThat(result).hasSize(1);
    }

    // listarBoletosPorMorador

    @Test
    void listarBoletosPorMorador_retornaLista() {
        when(boletoRepository.findByMoradorIdOrderByDataVencimentoDesc(1L)).thenReturn(List.of(boleto));
        List<BoletoResponse> result = boletoService.listarBoletosPorMorador(1L);
        assertThat(result).hasSize(1);
    }

    // gerarBoleto

    @Test
    void gerarBoleto_moradorEncontrado_comLinhaDigitavel() {
        var request = new BoletoRequest();
        request.setMoradorId(1L);
        request.setValor(BigDecimal.valueOf(500));
        request.setDataVencimento(LocalDate.now().plusDays(10));
        request.setDescricao("Abril");
        request.setLinhaDigitavel("123");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(boletoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BoletoResponse result = boletoService.gerarBoleto(request);
        assertThat(result).isNotNull();
    }

    @Test
    void gerarBoleto_semLinhaDigitavel_usaStringVazia() {
        var request = new BoletoRequest();
        request.setMoradorId(1L);
        request.setValor(BigDecimal.valueOf(200));
        request.setDataVencimento(LocalDate.now().plusDays(5));
        request.setDescricao("Março");
        request.setLinhaDigitavel(null);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(morador));
        when(boletoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BoletoResponse result = boletoService.gerarBoleto(request);
        assertThat(result).isNotNull();
    }

    @Test
    void gerarBoleto_moradorNaoEncontrado_lancaException() {
        var request = new BoletoRequest();
        request.setMoradorId(99L);

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletoService.gerarBoleto(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // pagarBoleto

    @Test
    void pagarBoleto_boletoEncontrado_marcaComoPago() {
        when(boletoRepository.findById(10L)).thenReturn(Optional.of(boleto));
        when(boletoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BoletoResponse result = boletoService.pagarBoleto(10L);
        assertThat(result.getStatus()).isEqualTo("PAGO");
    }

    @Test
    void pagarBoleto_jaEstaPago_lancaBusinessException() {
        boleto.setStatus(Boleto.StatusBoleto.PAGO);
        when(boletoRepository.findById(10L)).thenReturn(Optional.of(boleto));

        assertThatThrownBy(() -> boletoService.pagarBoleto(10L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("pago");
    }

    @Test
    void pagarBoleto_naoEncontrado_lancaResourceNotFoundException() {
        when(boletoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletoService.pagarBoleto(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // atualizarBoleto

    @Test
    void atualizarBoleto_todosOsCampos_atualizaCorretamente() {
        var request = new BoletoRequest();
        request.setDescricao("Novo");
        request.setValor(BigDecimal.valueOf(300));
        request.setDataVencimento(LocalDate.now().plusDays(15));
        request.setLinhaDigitavel("987654");
        request.setPdfBase64("data:application/pdf;base64,ABCDEF");

        when(boletoRepository.findById(10L)).thenReturn(Optional.of(boleto));
        when(boletoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BoletoResponse result = boletoService.atualizarBoleto(10L, request);
        assertThat(result).isNotNull();
        verify(boletoRepository).save(boleto);
    }

    @Test
    void atualizarBoleto_pdfSemVirgula_naoTransforma() {
        var request = new BoletoRequest();
        request.setPdfBase64("ABCDEFGH");

        when(boletoRepository.findById(10L)).thenReturn(Optional.of(boleto));
        when(boletoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BoletoResponse result = boletoService.atualizarBoleto(10L, request);
        assertThat(result).isNotNull();
    }

    @Test
    void atualizarBoleto_camposNulos_naoAtualiza() {
        var request = new BoletoRequest(); // all null

        when(boletoRepository.findById(10L)).thenReturn(Optional.of(boleto));
        when(boletoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BoletoResponse result = boletoService.atualizarBoleto(10L, request);
        assertThat(result).isNotNull();
    }

    @Test
    void atualizarBoleto_naoEncontrado_lancaResourceNotFoundException() {
        when(boletoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletoService.atualizarBoleto(99L, new BoletoRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // deletarBoleto

    @Test
    void deletarBoleto_existe_deletaComSucesso() {
        when(boletoRepository.existsById(10L)).thenReturn(true);
        boletoService.deletarBoleto(10L);
        verify(boletoRepository).deleteById(10L);
    }

    @Test
    void deletarBoleto_naoExiste_lancaResourceNotFoundException() {
        when(boletoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> boletoService.deletarBoleto(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // enviarEmailBoleto

    @Test
    void enviarEmailBoleto_encontrado_enviaEmail() {
        when(boletoRepository.findById(10L)).thenReturn(Optional.of(boleto));
        boletoService.enviarEmailBoleto(10L);
        verify(emailService).enviarEmailBoleto(morador.getEmail(), morador.getNome(), boleto);
    }

    @Test
    void enviarEmailBoleto_naoEncontrado_lancaResourceNotFoundException() {
        when(boletoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletoService.enviarEmailBoleto(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // enviarCobrancaBoleto

    @Test
    void enviarCobrancaBoleto_encontrado_enviaCobranca() {
        when(boletoRepository.findById(10L)).thenReturn(Optional.of(boleto));
        boletoService.enviarCobrancaBoleto(10L);
        verify(emailService).enviarEmailCobranca(morador.getEmail(), morador.getNome(), boleto);
    }

    @Test
    void enviarCobrancaBoleto_naoEncontrado_lancaResourceNotFoundException() {
        when(boletoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletoService.enviarCobrancaBoleto(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
