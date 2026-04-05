package com.condominium.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_acessos")
public class LogAcesso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String usuarioNome;

    @Column(nullable = false)
    private String usuarioEmail;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    private String ip;

    private String pagina;

    public LogAcesso() {}

    public LogAcesso(String usuarioNome, String usuarioEmail, String role, LocalDateTime dataHora, String ip) {
        this(usuarioNome, usuarioEmail, role, dataHora, ip, null);
    }

    public LogAcesso(String usuarioNome, String usuarioEmail, String role, LocalDateTime dataHora, String ip, String pagina) {
        this.usuarioNome = usuarioNome;
        this.usuarioEmail = usuarioEmail;
        this.role = role;
        this.dataHora = dataHora;
        this.ip = ip;
        this.pagina = pagina;
    }

    public Long getId() { return id; }
    public String getUsuarioNome() { return usuarioNome; }
    public String getUsuarioEmail() { return usuarioEmail; }
    public String getRole() { return role; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getIp() { return ip; }
    public String getPagina() { return pagina; }
}
