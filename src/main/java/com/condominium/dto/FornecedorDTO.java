package com.condominium.dto;

import com.condominium.model.Fornecedor;
import java.time.format.DateTimeFormatter;

public record FornecedorDTO(
    Long id,
    String nome,
    Long moradorId,
    String comentario,
    String vigencia,
    String contato,
    String valor,
    String dataCriacao,
    String dataAtualizacao
) {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static FornecedorDTO fromEntity(Fornecedor f) {
        return new FornecedorDTO(
            f.getId(),
            f.getNome(),
            f.getMoradorId(),
            f.getComentario(),
            f.getVigencia(),
            f.getContato(),
            f.getValor(),
            f.getDataCriacao() != null ? f.getDataCriacao().format(FMT) : null,
            f.getDataAtualizacao() != null ? f.getDataAtualizacao().format(FMT) : null
        );
    }
}
