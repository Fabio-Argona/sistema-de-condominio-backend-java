package com.condominium.service.impl;

import com.condominium.dto.VisitanteDTO;
import com.condominium.model.Visitante;

import java.util.List;

public interface IVisitanteService {
    List<VisitanteDTO> listar();
    VisitanteDTO criar(Visitante visitante);
    VisitanteDTO registrarSaida(Long id);
}
