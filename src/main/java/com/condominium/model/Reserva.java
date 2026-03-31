package com.condominium.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_comum_id", nullable = false)
    private AreaComum areaComum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "morador_id", nullable = false)
    private Usuario morador;

    @Column(nullable = false)
    private LocalDate dataReserva;

    @Column(nullable = false)
    private String horaInicio;

    @Column(nullable = false)
    private String horaFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusReserva status = StatusReserva.PENDENTE;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    public enum StatusReserva {
        PENDENTE, APROVADA, REJEITADA, CANCELADA
    }

    public Reserva() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AreaComum getAreaComum() { return areaComum; }
    public void setAreaComum(AreaComum areaComum) { this.areaComum = areaComum; }

    public Usuario getMorador() { return morador; }
    public void setMorador(Usuario morador) { this.morador = morador; }

    public LocalDate getDataReserva() { return dataReserva; }
    public void setDataReserva(LocalDate dataReserva) { this.dataReserva = dataReserva; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFim() { return horaFim; }
    public void setHoraFim(String horaFim) { this.horaFim = horaFim; }

    public StatusReserva getStatus() { return status; }
    public void setStatus(StatusReserva status) { this.status = status; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
