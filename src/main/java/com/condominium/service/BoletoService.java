package com.condominium.service;

import com.condominium.dto.BoletoRequest;
import com.condominium.dto.BoletoResponse;
import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.Boleto;
import com.condominium.model.Usuario;
import com.condominium.repository.BoletoRepository;
import com.condominium.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoletoService implements IBoletoService {

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Transactional(readOnly = true)
    public List<BoletoResponse> listarBoletos() {
        return boletoRepository.findAll().stream()
                .map(BoletoResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BoletoResponse> listarBoletosPorMorador(Long moradorId) {
        return boletoRepository.findByMoradorIdOrderByDataVencimentoDesc(moradorId).stream()
                .map(BoletoResponse::new)
                .collect(Collectors.toList());
    }

    public BoletoResponse gerarBoleto(BoletoRequest request) {
        Usuario morador = usuarioRepository.findById(request.getMoradorId())
                .orElseThrow(() -> new ResourceNotFoundException("Morador", request.getMoradorId()));

        Boleto boleto = new Boleto();
        boleto.setMorador(morador);
        boleto.setValor(request.getValor());
        boleto.setDataVencimento(request.getDataVencimento());
        boleto.setDescricao(request.getDescricao());
        boleto.setLinhaDigitavel(request.getLinhaDigitavel() != null ? request.getLinhaDigitavel() : "");
        boleto.setPdfBase64(request.getPdfBase64());
        boleto.setStatus(Boleto.StatusBoleto.PENDENTE);

        Boleto salvo = boletoRepository.save(boleto);
        return new BoletoResponse(salvo);
    }

    public BoletoResponse pagarBoleto(Long boletoId) {
        Boleto boleto = boletoRepository.findById(boletoId)
                .orElseThrow(() -> new ResourceNotFoundException("Boleto", boletoId));

        if (boleto.getStatus() == Boleto.StatusBoleto.PAGO) {
            throw new com.condominium.exception.BusinessException("Boleto já está pago");
        }

        boleto.setStatus(Boleto.StatusBoleto.PAGO);
        boleto.setDataPagamento(LocalDate.now());

        Boleto salvo = boletoRepository.save(boleto);
        return new BoletoResponse(salvo);
    }

    public BoletoResponse atualizarBoleto(Long boletoId, BoletoRequest request) {
        Boleto boleto = boletoRepository.findById(boletoId)
                .orElseThrow(() -> new ResourceNotFoundException("Boleto", boletoId));

        if (request.getDescricao() != null) boleto.setDescricao(request.getDescricao());
        if (request.getValor() != null) boleto.setValor(request.getValor());
        if (request.getDataVencimento() != null) boleto.setDataVencimento(request.getDataVencimento());
        if (request.getLinhaDigitavel() != null) boleto.setLinhaDigitavel(request.getLinhaDigitavel());
        if (request.getPdfBase64() != null && !request.getPdfBase64().isEmpty()) {
            String pdf = request.getPdfBase64();
            if (pdf.contains(",")) pdf = pdf.substring(pdf.indexOf(',') + 1);
            boleto.setPdfBase64(pdf);
        }

        Boleto salvo = boletoRepository.save(boleto);
        return new BoletoResponse(salvo);
    }

    public void deletarBoleto(Long boletoId) {
        if (!boletoRepository.existsById(boletoId)) {
            throw new ResourceNotFoundException("Boleto", boletoId);
        }
        boletoRepository.deleteById(boletoId);
    }
    public void enviarEmailBoleto(Long boletoId) {
        Boleto boleto = boletoRepository.findById(boletoId)
                .orElseThrow(() -> new ResourceNotFoundException("Boleto", boletoId));
        emailService.enviarEmailBoleto(boleto.getMorador().getEmail(), boleto.getMorador().getNome(), boleto);
    }

    public void enviarCobrancaBoleto(Long boletoId) {
        Boleto boleto = boletoRepository.findById(boletoId)
                .orElseThrow(() -> new ResourceNotFoundException("Boleto", boletoId));
        emailService.enviarEmailCobranca(boleto.getMorador().getEmail(), boleto.getMorador().getNome(), boleto);
    }
}
