package com.staya.api.dto;

import com.staya.api.model.CategoriaProduto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConsumoResponseDTO {

    private Long id;

    private Long produtoId;

    private String descricao;

    private CategoriaProduto categoria;

    private Integer quantidade;

    private BigDecimal valorUnitario;

    private BigDecimal valorTotal;

    private LocalDateTime lancadoEm;

    private String observacoes;

    private Boolean estornado;

    private LocalDateTime estornadoEm;

    private String motivoEstorno;

    public ConsumoResponseDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public CategoriaProduto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaProduto categoria) {
        this.categoria = categoria;
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
