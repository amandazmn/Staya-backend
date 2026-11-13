package com.staya.api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tipos_quarto")
public class TipoQuarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_quarto")
    private Long id;

    @Column(name = "nome", length = 50, nullable = false, unique = true)
    private String nome;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "capacidade", nullable = false)
    private Integer capacidade;

    @Column(name = "tarifa_base", precision = 10, scale = 2, nullable = false)
    private BigDecimal tarifaBase;

    public TipoQuarto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public BigDecimal getTarifaBase() {
        return tarifaBase;
    }

    public void setTarifaBase(BigDecimal tarifaBase) {
        this.tarifaBase = tarifaBase;
    }
}
