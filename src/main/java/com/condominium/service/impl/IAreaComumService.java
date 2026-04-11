package com.condominium.service.impl;

import com.condominium.model.AreaComum;

import java.util.List;

public interface IAreaComumService {
    List<AreaComum> listarTodas();
    AreaComum criar(AreaComum area);
    AreaComum atualizar(Long id, AreaComum areaDados);
    void deletar(Long id);
}
