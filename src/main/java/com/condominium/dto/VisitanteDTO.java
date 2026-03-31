package com.condominium.dto;

import com.condominium.model.Visitante;
import java.time.format.DateTimeFormatter;

public record VisitanteDTO(
    Long id,
    String nome,
    String documento,
    String telefone,
    String apartamento,
    String bloco,
    String moradorNome,
    String dataEntrada,
    String dataSaida,
    String veiculo,
    String placaVeiculo,
    String observacoes,
    String porteiroNome
) {
    public static VisitanteDTO fromEntity(Visitante visitante) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return new VisitanteDTO(
            visitante.getId(),
            visitante.getNome(),
            visitante.getDocumento(),
            visitante.getTelefone(),
            visitante.getApartamento(),
            visitante.getBloco(),
            visitante.getMoradorNome(),
            visitante.getDataEntrada() != null ? visitante.getDataEntrada().format(formatter) : null,
            visitante.getDataSaida() != null ? visitante.getDataSaida().format(formatter) : null,
            visitante.getVeiculo(),
            visitante.getPlacaVeiculo(),
            visitante.getObservacoes(),
            visitante.getPorteiroNome()
        );
    }
}
