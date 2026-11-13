package com.staya.api.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class TipoQuartoRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 50)
    private String nome;

    @Size(max = 255)
    private String descricao;

    @NotNull(message = "Capacidade é obrigatória")
    @Min(value = 1, message = "Capacidade deve ser de pelo menos 1 hóspede")
    @Max(value = 50)
    private Integer capacidade;

    @NotNull(message = "Tarifa base é obrigatória")
    @PositiveOrZero(message = "Tarifa base não pode ser negativa")
    @Digits(integer = 8, fraction = 2, message = "Tarifa base deve ter no máximo 8 dígitos inteiros e 2 decimais")
    private BigDecimal tarifaBase;

    public TipoQuartoRequestDTO() {}

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
