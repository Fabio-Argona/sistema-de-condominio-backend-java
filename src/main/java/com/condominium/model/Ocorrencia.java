package com.condominium.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ocorrencias")
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private String categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOcorrencia status = StatusOcorrencia.ABERTA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadeOcorrencia prioridade = PrioridadeOcorrencia.BAIXA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "morador_id", nullable = false)
    private Usuario morador;

    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao = LocalDateTime.now();

    @ElementCollection
    @CollectionTable(name = "ocorrencia_respostas", joinColumns = @JoinColumn(name = "ocorrencia_id"))
    @Column(name = "resposta", columnDefinition = "TEXT")
    private List<String> respostasSindico = new ArrayList<>();

    public enum StatusOcorrencia {
        ABERTA, EM_ANDAMENTO, RESOLVIDA, FECHADA
    }

    public enum PrioridadeOcorrencia {
        BAIXA, MEDIA, ALTA, URGENTE
    }

    public Ocorrencia() {}

    @PreUpdate
    public void preUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public StatusOcorrencia getStatus() { return status; }
    public void setStatus(StatusOcorrencia status) { this.status = status; }
    
    public PrioridadeOcorrencia getPrioridade() { return prioridade; }
    public void setPrioridade(PrioridadeOcorrencia prioridade) { this.prioridade = prioridade; }
    
    public Usuario getMorador() { return morador; }
    public void setMorador(Usuario morador) { this.morador = morador; }
    
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
    
    public List<String> getRespostasSindico() { return respostasSindico; }
    public void setRespostasSindico(List<String> respostasSindico) { this.respostasSindico = respostasSindico; }
}
