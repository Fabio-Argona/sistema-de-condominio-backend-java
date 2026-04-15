package com.condominium.dto;

import com.condominium.model.LogDownloadBoleto;

import java.time.LocalDateTime;

public class LogDownloadBoletoResponse {

    private Long id;
    private Long boletoId;
    private Long moradorId;
    private String moradorNome;
    private String descricaoBoleto;
    private Long usuarioId;
    private String usuarioNome;
    private String usuarioEmail;
    private String usuarioRole;
    private String usuarioRoleLabel;
    private LocalDateTime dataHoraDownload;

    public LogDownloadBoletoResponse(LogDownloadBoleto log) {
        this.id = log.getId();
        this.boletoId = log.getBoletoId();
        this.moradorId = log.getMoradorId();
        this.moradorNome = log.getMoradorNome();
        this.descricaoBoleto = log.getDescricaoBoleto();
        this.usuarioId = log.getUsuarioId();
        this.usuarioNome = log.getUsuarioNome();
        this.usuarioEmail = log.getUsuarioEmail();
        this.usuarioRole = log.getUsuarioRole();
        this.usuarioRoleLabel = labelFor(log.getUsuarioRole());
        this.dataHoraDownload = log.getDataHoraDownload();
    }

    private String labelFor(String role) {
        return switch (role) {
            case "SINDICO" -> "Síndico";
            case "MORADOR" -> "Usuário";
            case "PORTEIRO" -> "Porteiro";
            default -> role;
        };
    }

    public Long getId() {
        return id;
    }

    public Long getBoletoId() {
        return boletoId;
    }

    public Long getMoradorId() {
        return moradorId;
    }

    public String getMoradorNome() {
        return moradorNome;
    }

    public String getDescricaoBoleto() {
        return descricaoBoleto;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public String getUsuarioRole() {
        return usuarioRole;
    }

    public String getUsuarioRoleLabel() {
        return usuarioRoleLabel;
    }

    public LocalDateTime getDataHoraDownload() {
        return dataHoraDownload;
    }
}