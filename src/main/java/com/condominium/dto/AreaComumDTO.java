package com.condominium.dto;

import com.condominium.model.AreaComum;

public record AreaComumDTO(
    Long id,
    String nome,
    String descricao,
    Integer capacidade,
    Double valorReserva,
    String horarioAbertura,
    String horarioFechamento,
    Boolean disponivel
) {
    public static AreaComumDTO fromEntity(AreaComum areaComum) {
        return new AreaComumDTO(
            areaComum.getId(),
            areaComum.getNome(),
            areaComum.getDescricao(),
            areaComum.getCapacidade(),
            areaComum.getValorReserva(),
            areaComum.getHorarioAbertura(),
            areaComum.getHorarioFechamento(),
            areaComum.getDisponivel()
        );
    }
}
