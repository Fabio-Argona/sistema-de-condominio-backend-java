package com.condominium.dto;

import com.condominium.model.Comunicado;
import java.time.format.DateTimeFormatter;

public record ComunicadoDTO(
    Long id,
    String titulo,
    String conteudo,
    String autor,
    String dataCriacao,
    boolean importante,
    String categoria
) {
    public static ComunicadoDTO fromEntity(Comunicado comunicado) {
        return new ComunicadoDTO(
            comunicado.getId(),
            comunicado.getTitulo(),
            comunicado.getConteudo(),
            comunicado.getAutor(),
            comunicado.getDataCriacao().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            comunicado.isImportante(),
            comunicado.getCategoria()
        );
    }
}
