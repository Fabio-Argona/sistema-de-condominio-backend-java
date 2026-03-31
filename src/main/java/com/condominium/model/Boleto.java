package com.condominium.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "boletos")
public class Boleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "morador_id", nullable = false)
    private Usuario morador;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDate dataVencimento;

    @Column
    private LocalDate dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusBoleto status = StatusBoleto.PENDENTE;

    @Column(nullable = false, length = 100)
    private String linhaDigitavel;

    @Column(nullable = false, length = 150)
    private String descricao;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String pdfBase64;

    public enum StatusBoleto {
        PENDENTE, PAGO, VENCIDO
    }

    public Boleto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getMorador() { return morador; }
    public void setMorador(Usuario morador) { this.morador = morador; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }

    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }

    public StatusBoleto getStatus() { return status; }
    public void setStatus(StatusBoleto status) { this.status = status; }

    public String getLinhaDigitavel() { return linhaDigitavel; }
    public void setLinhaDigitavel(String linhaDigitavel) { this.linhaDigitavel = linhaDigitavel; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getPdfBase64() { return pdfBase64; }
    public void setPdfBase64(String pdfBase64) { this.pdfBase64 = pdfBase64; }
}
