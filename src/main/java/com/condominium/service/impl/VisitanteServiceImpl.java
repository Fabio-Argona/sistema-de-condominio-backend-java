package com.condominium.service.impl;

import com.condominium.dto.VisitanteDTO;
import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.Visitante;
import com.condominium.repository.VisitanteRepository;
import com.condominium.service.IVisitanteService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitanteServiceImpl implements IVisitanteService {

    private final VisitanteRepository visitanteRepository;

    public VisitanteServiceImpl(VisitanteRepository visitanteRepository) {
        this.visitanteRepository = visitanteRepository;
    }

    @Override
    public List<VisitanteDTO> listar() {
        return visitanteRepository.findAllByOrderByDataEntradaDesc().stream()
                .map(VisitanteDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public VisitanteDTO criar(Visitante visitante) {
        visitante.setDataEntrada(LocalDateTime.now());
        return VisitanteDTO.fromEntity(visitanteRepository.save(visitante));
    }

    @Override
    public VisitanteDTO registrarSaida(Long id) {
        var visitante = visitanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitante", id));
        visitante.setDataSaida(LocalDateTime.now());
        return VisitanteDTO.fromEntity(visitanteRepository.save(visitante));
    }
}
