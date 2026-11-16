package com.staya.api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Item lançado na conta da estadia. Descrição e preço são copiados do produto no momento do lançamento,
 * para que mudanças no catálogo não alterem contas já lançadas.
 */
@Entity
@Table(name = "consumos")
public class Consumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consumo")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_estadia", nullable = false)
    private Estadia estadia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_produto", nullable = false)
    private Produto produto;

    @Column(name = "descricao", length = 100, nullable = false)
    private String descricao;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "valor_unitario", precision = 10, scale = 2, nullable = false)
    private BigDecimal valorUnitario;

    @Column(name = "valor_total", precision = 12, scale = 2, nullable = false)
    private BigDecimal valorTotal;

    @Column(name = "lancado_em", nullable = false)
    private LocalDateTime lancadoEm;

    @Column(name = "observacoes", length = 255)
    private String observacoes;

    @Column(name = "estornado_em")
    private LocalDateTime estornadoEm;

    @Column(name = "motivo_estorno", length = 255)
    private String motivoEstorno;

    public Consumo() {}

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

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDateTime getLancadoEm() {
        return lancadoEm;
    }

    public void setLancadoEm(LocalDateTime lancadoEm) {
        this.lancadoEm = lancadoEm;
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
