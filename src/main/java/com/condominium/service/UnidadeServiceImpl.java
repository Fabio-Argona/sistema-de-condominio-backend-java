package com.condominium.service;

import com.condominium.dto.UnidadeDTO;
import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.Unidade;
import com.condominium.repository.UnidadeRepository;
import com.condominium.service.impl.IUnidadeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UnidadeServiceImpl implements IUnidadeService {

    private final UnidadeRepository unidadeRepository;

    public UnidadeServiceImpl(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

    @Override
    public List<UnidadeDTO> listarTodas() {
        return unidadeRepository.findAll().stream()
                .map(UnidadeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public UnidadeDTO criar(Unidade unidade) {
        return UnidadeDTO.fromEntity(unidadeRepository.save(unidade));
    }

    @Override
    public UnidadeDTO buscarPorId(Long id) {
        var unidade = unidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade", id));
        return UnidadeDTO.fromEntity(unidade);
    }
}
