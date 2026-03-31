package com.condominium.dto;

import com.condominium.model.Unidade;

public record UnidadeDTO(
    Long id,
    String numero,
    String bloco,
    String status,
    Long moradorId,
    String moradorNome,
    Long proprietarioId,
    String proprietarioNome
) {
    public static UnidadeDTO fromEntity(Unidade unidade) {
        return new UnidadeDTO(
            unidade.getId(),
            unidade.getNumero(),
            unidade.getBloco(),
            unidade.getStatus().name(),
            unidade.getMorador() != null ? unidade.getMorador().getId() : null,
            unidade.getMorador() != null ? unidade.getMorador().getNome() : null,
            unidade.getProprietario() != null ? unidade.getProprietario().getId() : null,
            unidade.getProprietario() != null ? unidade.getProprietario().getNome() : null
        );
    }
}
