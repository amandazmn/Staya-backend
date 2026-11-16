package com.staya.api.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pagamento")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_estadia", nullable = false)
    private Estadia estadia;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "forma", length = 20, nullable = false)
    private FormaPagamento forma;

    @Column(name = "valor", precision = 12, scale = 2, nullable = false)
    private BigDecimal valor;

    @Column(name = "pago_em", nullable = false)
    private LocalDateTime pagoEm;

    @Column(name = "observacoes", length = 255)
    private String observacoes;

    @Column(name = "estornado_em")
    private LocalDateTime estornadoEm;

    @Column(name = "motivo_estorno", length = 255)
    private String motivoEstorno;

    public Pagamento() {}

    public boolean isEstornado() {
        return estornadoEm != null;
    }

    public void estornar(LocalDateTime quando, String motivo) {
        this.estornadoEm = quando;
        this.motivoEstorno = motivo;
    }

    public Long getId() {
        return id;
    }

    public Estadia getEstadia() {
        return estadia;
    }

    public void setEstadia(Estadia estadia) {
        this.estadia = estadia;
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

    public LocalDateTime getEstornadoEm() {
        return estornadoEm;
    }

    public String getMotivoEstorno() {
        return motivoEstorno;
    }
}
