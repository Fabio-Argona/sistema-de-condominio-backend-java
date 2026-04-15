package com.condominium.dto;

import com.condominium.model.Ocorrencia;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public record OcorrenciaDTO(
    Long id,
    String titulo,
    String descricao,
    String categoria,
    String status,
    String prioridade,
    Long moradorId,
    String moradorNome,
    Long profissionalResponsavelId,
    String profissionalResponsavelNome,
    String apartamento,
    String bloco,
    String dataCriacao,
    String dataAtualizacao,
    List<String> respostasSindico
) {
    public static OcorrenciaDTO fromEntity(Ocorrencia ocorrencia) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        
        return new OcorrenciaDTO(
            ocorrencia.getId(),
            ocorrencia.getTitulo(),
            ocorrencia.getDescricao(),
            ocorrencia.getCategoria(),
            ocorrencia.getStatus().name(),
            ocorrencia.getPrioridade().name(),
            ocorrencia.getMorador().getId(),
            ocorrencia.getMorador().getNome(),
            ocorrencia.getProfissionalResponsavel() != null ? ocorrencia.getProfissionalResponsavel().getId() : null,
            ocorrencia.getProfissionalResponsavel() != null ? ocorrencia.getProfissionalResponsavel().getNome() : null,
            ocorrencia.getMorador().getApartamento(),
            ocorrencia.getMorador().getBloco(),
            ocorrencia.getDataCriacao().format(formatter),
            ocorrencia.getDataAtualizacao().format(formatter),
            new ArrayList<>(ocorrencia.getRespostasSindico())
        );
    }
}
