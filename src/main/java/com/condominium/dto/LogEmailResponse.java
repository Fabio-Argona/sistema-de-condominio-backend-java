package com.condominium.dto;

import com.condominium.model.LogEmail;
import java.time.LocalDateTime;

public class LogEmailResponse {

    private Long id;
    private String tipo;
    private String tipoLabel;
    private String destinatario;
    private String destinatarioNome;
    private LocalDateTime dataHoraEnvio;
    private Long boletoId;
    private String descricao;

    public LogEmailResponse(LogEmail log) {
        this.id = log.getId();
        this.tipo = log.getTipo().name();
        this.tipoLabel = labelFor(log.getTipo());
        this.destinatario = log.getDestinatario();
        this.destinatarioNome = log.getDestinatarioNome();
        this.dataHoraEnvio = log.getDataHoraEnvio();
        this.boletoId = log.getBoletoId();
        this.descricao = log.getDescricao();
    }

    private String labelFor(LogEmail.TipoEmail tipo) {
        return switch (tipo) {
            case ENVIO_BOLETO -> "Envio de Boleto";
            case COBRANCA_BOLETO_VENCIDO -> "Cobrança - Boleto Vencido";
            case RECUPERACAO_SENHA -> "Recuperação de Acesso";
            case SENHA_ALTERADA -> "Alteração de Senha";
            case CONVITE_MORADOR -> "Convite de Morador";
            case NOVA_RESERVA -> "Notificação de Reserva";
            case STATUS_RESERVA -> "Atualização de Reserva";
        };
    }

    public Long getId() { return id; }
    public String getTipo() { return tipo; }
    public String getTipoLabel() { return tipoLabel; }
    public String getDestinatario() { return destinatario; }
    public String getDestinatarioNome() { return destinatarioNome; }
    public LocalDateTime getDataHoraEnvio() { return dataHoraEnvio; }
    public Long getBoletoId() { return boletoId; }
    public String getDescricao() { return descricao; }
}
