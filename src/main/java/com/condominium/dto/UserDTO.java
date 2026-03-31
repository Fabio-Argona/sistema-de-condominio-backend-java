package com.condominium.dto;

import com.condominium.model.Usuario;

public record UserDTO(
    Long id,
    String nome,
    String email,
    String role,
    String apartamento,
    String bloco,
    String telefone,
    String cpf,
    boolean ativo
) {
    public static UserDTO fromEntity(Usuario usuario) {
        return new UserDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getRole().name(),
            usuario.getApartamento(),
            usuario.getBloco(),
            usuario.getTelefone(),
            usuario.getCpf(),
            usuario.isAtivo()
        );
    }
}
