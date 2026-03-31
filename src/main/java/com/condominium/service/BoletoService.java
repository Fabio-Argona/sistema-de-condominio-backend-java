package com.condominium.service;

import com.condominium.dto.BoletoRequest;
import com.condominium.dto.BoletoResponse;
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
public class BoletoService {

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

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
                .orElseThrow(() -> new RuntimeException("Morador não encontrado"));

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
                .orElseThrow(() -> new RuntimeException("Boleto não encontrado"));

        if (boleto.getStatus() == Boleto.StatusBoleto.PAGO) {
            throw new RuntimeException("Boleto já está pago");
        }

        boleto.setStatus(Boleto.StatusBoleto.PAGO);
        boleto.setDataPagamento(LocalDate.now());

        Boleto salvo = boletoRepository.save(boleto);
        return new BoletoResponse(salvo);
    }

    public void deletarBoleto(Long boletoId) {
        if (!boletoRepository.existsById(boletoId)) {
            throw new RuntimeException("Boleto não encontrado");
        }
        boletoRepository.deleteById(boletoId);
    }
}
