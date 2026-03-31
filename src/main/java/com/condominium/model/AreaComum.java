package com.condominium.model;

import jakarta.persistence.*;

@Entity
@Table(name = "areas_comuns")
public class AreaComum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    private String descricao;
    private Integer capacidade;
    private Double valorReserva;

    @Column(nullable = false)
    private String horarioAbertura = "08:00";

    @Column(nullable = false)
    private String horarioFechamento = "22:00";

    @Column(nullable = false)
    private Boolean disponivel = true;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }

    public Double getValorReserva() { return valorReserva; }
    public void setValorReserva(Double valorReserva) { this.valorReserva = valorReserva; }

    public String getHorarioAbertura() { return horarioAbertura; }
    public void setHorarioAbertura(String horarioAbertura) { this.horarioAbertura = horarioAbertura; }

    public String getHorarioFechamento() { return horarioFechamento; }
    public void setHorarioFechamento(String horarioFechamento) { this.horarioFechamento = horarioFechamento; }

    public Boolean getDisponivel() { return disponivel; }
    public void setDisponivel(Boolean disponivel) { this.disponivel = disponivel; }
}
