package com.condominium.service.impl;

import com.condominium.dto.ComunicadoDTO;
import com.condominium.model.Comunicado;

import java.util.List;

public interface IComunicadoService {
    List<ComunicadoDTO> listarTodos();
    ComunicadoDTO criar(Comunicado comunicado);
    ComunicadoDTO atualizar(Long id, Comunicado comunicadoAtualizado);
    void remover(Long id);
}
