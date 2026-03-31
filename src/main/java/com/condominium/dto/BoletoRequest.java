package com.condominium.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BoletoRequest {
    private Long moradorId;
    private BigDecimal valor;
    private LocalDate dataVencimento;
    private String descricao;
    private String linhaDigitavel;
    private String pdfBase64;

    public BoletoRequest() {}

    public Long getMoradorId() { return moradorId; }
    public void setMoradorId(Long moradorId) { this.moradorId = moradorId; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getLinhaDigitavel() { return linhaDigitavel; }
    public void setLinhaDigitavel(String linhaDigitavel) { this.linhaDigitavel = linhaDigitavel; }

    public String getPdfBase64() { return pdfBase64; }
    public void setPdfBase64(String pdfBase64) { this.pdfBase64 = pdfBase64; }
}
