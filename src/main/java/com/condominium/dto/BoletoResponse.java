package com.condominium.dto;

import com.condominium.model.Boleto;
import java.math.BigDecimal;
import java.time.LocalDate;

public class BoletoResponse {
    private Long id;
    private Long moradorId;
    private String moradorNome;
    private BigDecimal valor;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private String status;
    private String linhaDigitavel;
    private String descricao;
    private String pdfBase64;

    public BoletoResponse() {}

    public BoletoResponse(Boleto boleto) {
        this.id = boleto.getId();
        if (boleto.getMorador() != null) {
            this.moradorId = boleto.getMorador().getId();
            this.moradorNome = boleto.getMorador().getNome();
        }
        this.valor = boleto.getValor();
        this.dataVencimento = boleto.getDataVencimento();
        this.dataPagamento = boleto.getDataPagamento();
        this.status = boleto.getStatus().name();
        
        // Regra de Negócio: Se estiver PENDENTE mas a data já passou, exibir como VENCIDO
        if (boleto.getStatus() == Boleto.StatusBoleto.PENDENTE && 
            this.dataVencimento != null && 
            this.dataVencimento.isBefore(LocalDate.now())) {
            this.status = "VENCIDO";
        }
        
        this.linhaDigitavel = boleto.getLinhaDigitavel();
        this.descricao = boleto.getDescricao();
        this.pdfBase64 = boleto.getPdfBase64();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMoradorId() { return moradorId; }
    public void setMoradorId(Long moradorId) { this.moradorId = moradorId; }

    public String getMoradorNome() { return moradorNome; }
    public void setMoradorNome(String moradorNome) { this.moradorNome = moradorNome; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }

    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLinhaDigitavel() { return linhaDigitavel; }
    public void setLinhaDigitavel(String linhaDigitavel) { this.linhaDigitavel = linhaDigitavel; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getPdfBase64() { return pdfBase64; }
    public void setPdfBase64(String pdfBase64) { this.pdfBase64 = pdfBase64; }
}
