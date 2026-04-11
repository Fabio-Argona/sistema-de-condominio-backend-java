package com.condominium.service.impl;

import com.condominium.dto.OcorrenciaDTO;
import com.condominium.model.Ocorrencia;

import java.util.List;

public interface IOcorrenciaService {
    List<OcorrenciaDTO> listarTodas();
    List<OcorrenciaDTO> listarPorMorador(Long moradorId);
    OcorrenciaDTO criar(Long moradorId, Ocorrencia ocorrencia);
    OcorrenciaDTO atualizarStatus(Long id, Ocorrencia dadosAtualizacao);
}
