package com.condominium.service.impl;

import com.condominium.dto.FornecedorDTO;
import com.condominium.model.Fornecedor;

import java.util.List;

public interface IFornecedorService {
    List<FornecedorDTO> listarTodos();
    List<FornecedorDTO> listarPorMorador(Long moradorId);
    FornecedorDTO criar(Fornecedor fornecedor);
    FornecedorDTO criarParaMorador(Long moradorId, Fornecedor fornecedor);
    FornecedorDTO atualizar(Long id, Fornecedor dados);
    FornecedorDTO atualizarParaMorador(Long moradorId, Long id, Fornecedor dados);
    void deletar(Long id);
    void deletarParaMorador(Long moradorId, Long id);
}
