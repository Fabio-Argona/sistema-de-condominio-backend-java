package com.condominium.service;

import com.condominium.dto.OcorrenciaDTO;
import com.condominium.exception.BusinessException;
import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.Ocorrencia;
import com.condominium.model.Usuario;
import com.condominium.repository.OcorrenciaRepository;
import com.condominium.repository.UsuarioRepository;
import com.condominium.service.impl.IOcorrenciaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OcorrenciaServiceImpl implements IOcorrenciaService {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final UsuarioRepository usuarioRepository;

    public OcorrenciaServiceImpl(OcorrenciaRepository ocorrenciaRepository, UsuarioRepository usuarioRepository) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<OcorrenciaDTO> listarTodas() {
        return ocorrenciaRepository.findAllByOrderByDataCriacaoDesc().stream()
                .map(OcorrenciaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<OcorrenciaDTO> listarPorMorador(Long moradorId) {
        return ocorrenciaRepository.findByMoradorIdOrderByDataCriacaoDesc(moradorId).stream()
                .map(OcorrenciaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<OcorrenciaDTO> listarPorProfissional(Long profissionalId) {
        return ocorrenciaRepository.findByProfissionalResponsavel_IdOrderByDataCriacaoDesc(profissionalId).stream()
                .map(OcorrenciaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public OcorrenciaDTO criar(Long moradorId, Ocorrencia ocorrencia) {
        var morador = usuarioRepository.findById(moradorId)
                .orElseThrow(() -> new ResourceNotFoundException("Morador", moradorId));
        ocorrencia.setMorador(morador);
        vincularProfissionalResponsavel(ocorrencia, ocorrencia.getProfissionalResponsavelId());
        if (ocorrencia.getStatus() == null) ocorrencia.setStatus(Ocorrencia.StatusOcorrencia.ABERTA);
        if (ocorrencia.getPrioridade() == null) ocorrencia.setPrioridade(Ocorrencia.PrioridadeOcorrencia.BAIXA);
        return OcorrenciaDTO.fromEntity(ocorrenciaRepository.save(ocorrencia));
    }

    @Override
    public OcorrenciaDTO atualizarStatus(Long id, Ocorrencia dadosAtualizacao) {
        var ocorrencia = ocorrenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência", id));
        if (dadosAtualizacao.getStatus() != null) {
            ocorrencia.setStatus(dadosAtualizacao.getStatus());
        }
        if (dadosAtualizacao.getProfissionalResponsavelId() != null) {
            vincularProfissionalResponsavel(ocorrencia, dadosAtualizacao.getProfissionalResponsavelId());
        }
        if (dadosAtualizacao.getRespostasSindico() != null && !dadosAtualizacao.getRespostasSindico().isEmpty()) {
            ocorrencia.getRespostasSindico().addAll(dadosAtualizacao.getRespostasSindico());
        }
        return OcorrenciaDTO.fromEntity(ocorrenciaRepository.save(ocorrencia));
    }

    private void vincularProfissionalResponsavel(Ocorrencia ocorrencia, Long profissionalId) {
        if (profissionalId == null) {
            return;
        }

        Usuario profissional = usuarioRepository.findById(profissionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional", profissionalId));

        if (profissional.getRole() != Usuario.Role.MANTENEDOR) {
            throw new BusinessException("O usuário informado não possui perfil de mantenedor.");
        }

        ocorrencia.setProfissionalResponsavel(profissional);
    }
}
