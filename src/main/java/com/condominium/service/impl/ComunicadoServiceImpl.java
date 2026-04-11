package com.condominium.service.impl;

import com.condominium.dto.ComunicadoDTO;
import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.Comunicado;
import com.condominium.repository.ComunicadoRepository;
import com.condominium.service.IComunicadoService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComunicadoServiceImpl implements IComunicadoService {

    private final ComunicadoRepository comunicadoRepository;

    public ComunicadoServiceImpl(ComunicadoRepository comunicadoRepository) {
        this.comunicadoRepository = comunicadoRepository;
    }

    @Override
    public List<ComunicadoDTO> listarTodos() {
        return comunicadoRepository.findAllByOrderByDataCriacaoDesc().stream()
                .map(ComunicadoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ComunicadoDTO criar(Comunicado comunicado) {
        comunicado.setDataCriacao(LocalDateTime.now());
        if (comunicado.getAutor() == null) {
            comunicado.setAutor("Administração");
        }
        return ComunicadoDTO.fromEntity(comunicadoRepository.save(comunicado));
    }

    @Override
    public ComunicadoDTO atualizar(Long id, Comunicado comunicadoAtualizado) {
        var comunicado = comunicadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comunicado", id));
        comunicado.setTitulo(comunicadoAtualizado.getTitulo());
        comunicado.setConteudo(comunicadoAtualizado.getConteudo());
        comunicado.setCategoria(comunicadoAtualizado.getCategoria());
        comunicado.setImportante(comunicadoAtualizado.isImportante());
        if (comunicadoAtualizado.getAutor() != null) {
            comunicado.setAutor(comunicadoAtualizado.getAutor());
        }
        return ComunicadoDTO.fromEntity(comunicadoRepository.save(comunicado));
    }

    @Override
    public void remover(Long id) {
        if (!comunicadoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comunicado", id);
        }
        comunicadoRepository.deleteById(id);
    }
}
