package com.condominium.service;

import com.condominium.dto.UnidadeDTO;
import com.condominium.model.Unidade;

import java.util.List;

public interface IUnidadeService {
    List<UnidadeDTO> listarTodas();
    UnidadeDTO criar(Unidade unidade);
    UnidadeDTO buscarPorId(Long id);
}
