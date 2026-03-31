package com.condominium.dto;

import com.condominium.model.Reserva;
import java.time.format.DateTimeFormatter;

public record ReservaDTO(
    Long id,
    Long areaComumId,
    String areaComumNome,
    Long moradorId,
    String moradorNome,
    String apartamento,
    String bloco,
    String dataReserva,
    String horaInicio,
    String horaFim,
    String status,
    String observacoes,
    String dataCriacao
) {
    public static ReservaDTO fromEntity(Reserva reserva) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        return new ReservaDTO(
            reserva.getId(),
            reserva.getAreaComum().getId(),
            reserva.getAreaComum().getNome(),
            reserva.getMorador().getId(),
            reserva.getMorador().getNome(),
            reserva.getMorador().getApartamento(),
            reserva.getMorador().getBloco(),
            reserva.getDataReserva().format(dateFormatter),
            reserva.getHoraInicio(),
            reserva.getHoraFim(),
            reserva.getStatus().name(),
            reserva.getObservacoes(),
            reserva.getDataCriacao().format(dateTimeFormatter)
        );
    }
}
