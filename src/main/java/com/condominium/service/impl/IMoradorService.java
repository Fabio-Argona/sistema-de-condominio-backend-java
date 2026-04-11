package com.condominium.service.impl;

import com.condominium.dto.UserDTO;
import com.condominium.model.Usuario;

import java.util.List;
import java.util.Map;

public interface IMoradorService {
    List<UserDTO> listarTodos();
    Map<String, Object> criar(Usuario morador);
    Map<String, Object> reenviarConvite(Long id);
    UserDTO atualizar(Long id, Usuario moradorAtualizado);
    Map<String, Object> remover(Long id);
    UserDTO alternarStatus(Long id);
    UserDTO alterarRole(Long id, String role);
}
