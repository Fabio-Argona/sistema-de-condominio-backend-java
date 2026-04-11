package com.condominium.service.impl;

import com.condominium.dto.ReservaDTO;
import com.condominium.model.Reserva;

import java.util.List;

public interface IReservaService {
    List<ReservaDTO> listarTodas();
    List<ReservaDTO> listarPorMorador(Long moradorId);
    Object criar(Long moradorId, Long areaId, Reserva reserva);
    ReservaDTO atualizarStatus(Long id, Reserva dadosAtualizacao);
}
