package com.condominium.service;

import com.condominium.dto.BoletoRequest;
import com.condominium.dto.BoletoResponse;

import java.util.List;

public interface IBoletoService {
    List<BoletoResponse> listarBoletos();
    List<BoletoResponse> listarBoletosPorMorador(Long moradorId);
    BoletoResponse gerarBoleto(BoletoRequest request);
    BoletoResponse pagarBoleto(Long boletoId);
    BoletoResponse atualizarBoleto(Long boletoId, BoletoRequest request);
    void deletarBoleto(Long boletoId);
    void enviarEmailBoleto(Long boletoId);
    void enviarCobrancaBoleto(Long boletoId);
}
