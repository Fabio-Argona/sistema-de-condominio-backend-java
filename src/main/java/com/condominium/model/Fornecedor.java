package com.condominium.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fornecedores")
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private Long moradorId;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    private String vigencia;

    private String contato;

    private String valor;

    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao = LocalDateTime.now();

    public Fornecedor() {}

    @PreUpdate
    public void preUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Long getMoradorId() { return moradorId; }
    public void setMoradorId(Long moradorId) { this.moradorId = moradorId; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public String getVigencia() { return vigencia; }
    public void setVigencia(String vigencia) { this.vigencia = vigencia; }

    public String getContato() { return contato; }
    public void setContato(String contato) { this.contato = contato; }

    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}
