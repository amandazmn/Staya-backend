package com.staya.api.dto;

import com.staya.api.model.FormaPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagamentoResponseDTO {

    private Long id;

    private FormaPagamento forma;

    private BigDecimal valor;

    private LocalDateTime pagoEm;

    private String observacoes;

    private Boolean estornado;

    private LocalDateTime estornadoEm;

    private String motivoEstorno;

    public PagamentoResponseDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FormaPagamento getForma() {
        return forma;
    }

    public void setForma(FormaPagamento forma) {
        this.forma = forma;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDateTime getPagoEm() {
        return pagoEm;
    }

    public void setPagoEm(LocalDateTime pagoEm) {
        this.pagoEm = pagoEm;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Boolean getEstornado() {
        return estornado;
    }

    public void setEstornado(Boolean estornado) {
        this.estornado = estornado;
    }

    public LocalDateTime getEstornadoEm() {
        return estornadoEm;
    }

    public void setEstornadoEm(LocalDateTime estornadoEm) {
        this.estornadoEm = estornadoEm;
    }

    public String getMotivoEstorno() {
        return motivoEstorno;
    }

    public void setMotivoEstorno(String motivoEstorno) {
        this.motivoEstorno = motivoEstorno;
    }
}
