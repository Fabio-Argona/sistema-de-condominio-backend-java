package com.condominium.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_emails")
public class LogEmail {

    public enum TipoEmail {
        ENVIO_BOLETO,
        COBRANCA_BOLETO_VENCIDO,
        RECUPERACAO_SENHA,
        SENHA_ALTERADA,
        CONVITE_MORADOR,
        NOVA_RESERVA,
        STATUS_RESERVA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoEmail tipo;

    @Column(nullable = false)
    private String destinatario;

    @Column(nullable = false)
    private String destinatarioNome;

    @Column(nullable = false)
    private LocalDateTime dataHoraEnvio;

    /** Referência opcional ao boleto (nullable) */
    @Column
    private Long boletoId;

    /** Descrição curta para exibição */
    @Column(length = 255)
    private String descricao;

    public LogEmail() {}

    public LogEmail(TipoEmail tipo, String destinatario, String destinatarioNome,
                    LocalDateTime dataHoraEnvio, Long boletoId, String descricao) {
        this.tipo = tipo;
        this.destinatario = destinatario;
        this.destinatarioNome = destinatarioNome;
        this.dataHoraEnvio = dataHoraEnvio;
        this.boletoId = boletoId;
        this.descricao = descricao;
    }

    public Long getId() { return id; }
    public TipoEmail getTipo() { return tipo; }
    public String getDestinatario() { return destinatario; }
    public String getDestinatarioNome() { return destinatarioNome; }
    public LocalDateTime getDataHoraEnvio() { return dataHoraEnvio; }
    public Long getBoletoId() { return boletoId; }
    public String getDescricao() { return descricao; }
}
