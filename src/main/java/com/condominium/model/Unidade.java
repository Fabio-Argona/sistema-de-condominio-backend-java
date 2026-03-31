package com.condominium.model;

import jakarta.persistence.*;

@Entity
@Table(name = "unidades")
public class Unidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false)
    private String bloco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusUnidade status = StatusUnidade.DISPONIVEL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "morador_id")
    private Usuario morador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proprietario_id")
    private Usuario proprietario;

    public enum StatusUnidade {
        DISPONIVEL, ALUGADO, PROPRIETARIO_MORANDO, REFORMA
    }

    // Construtores
    public Unidade() {}

    public Unidade(String numero, String bloco) {
        this.numero = numero;
        this.bloco = bloco;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getBloco() { return bloco; }
    public void setBloco(String bloco) { this.bloco = bloco; }

    public StatusUnidade getStatus() { return status; }
    public void setStatus(StatusUnidade status) { this.status = status; }

    public Usuario getMorador() { return morador; }
    public void setMorador(Usuario morador) { this.morador = morador; }

    public Usuario getProprietario() { return proprietario; }
    public void setProprietario(Usuario proprietario) { this.proprietario = proprietario; }
}
