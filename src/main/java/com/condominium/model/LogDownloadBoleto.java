package com.condominium.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "log_downloads_boletos")
public class LogDownloadBoleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long boletoId;

    @Column(nullable = false)
    private Long moradorId;

    @Column(nullable = false)
    private String moradorNome;

    @Column(nullable = false, length = 150)
    private String descricaoBoleto;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private String usuarioNome;

    @Column(nullable = false)
    private String usuarioEmail;

    @Column(nullable = false, length = 20)
    private String usuarioRole;

    @Column(nullable = false)
    private LocalDateTime dataHoraDownload;

    public LogDownloadBoleto() {
    }

    public LogDownloadBoleto(Long boletoId, Long moradorId, String moradorNome, String descricaoBoleto,
                             Long usuarioId, String usuarioNome, String usuarioEmail, String usuarioRole,
                             LocalDateTime dataHoraDownload) {
        this.boletoId = boletoId;
        this.moradorId = moradorId;
        this.moradorNome = moradorNome;
        this.descricaoBoleto = descricaoBoleto;
        this.usuarioId = usuarioId;
        this.usuarioNome = usuarioNome;
        this.usuarioEmail = usuarioEmail;
        this.usuarioRole = usuarioRole;
        this.dataHoraDownload = dataHoraDownload;
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

    public LocalDateTime getDataHoraDownload() {
        return dataHoraDownload;
    }
}