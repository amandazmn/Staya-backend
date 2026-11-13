package com.staya.api.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ReservaRequestDTO {

    @NotNull(message = "Cliente é obrigatório")
    private Long clienteId;

    @NotNull(message = "Quarto é obrigatório")
    private Long quartoId;

    @NotNull(message = "Data de entrada é obrigatória")
    @FutureOrPresent(message = "Data de entrada não pode estar no passado")
    private LocalDate dataEntrada;

    @NotNull(message = "Data de saída é obrigatória")
    private LocalDate dataSaida;

    @NotNull(message = "Quantidade de hóspedes é obrigatória")
    @Min(value = 1, message = "A reserva deve ter pelo menos 1 hóspede")
    private Integer quantidadeHospedes;

    @Size(max = 255)
    private String observacoes;

    public ReservaRequestDTO() {}

    @AssertTrue(message = "Data de saída deve ser posterior à data de entrada")
    public boolean isPeriodoValido() {
        return dataEntrada == null || dataSaida == null || dataSaida.isAfter(dataEntrada);
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getQuartoId() {
        return quartoId;
    }

    public void setQuartoId(Long quartoId) {
        this.quartoId = quartoId;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDate getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDate dataSaida) {
        this.dataSaida = dataSaida;
    }

    public Integer getQuantidadeHospedes() {
        return quantidadeHospedes;
    }

    public void setQuantidadeHospedes(Integer quantidadeHospedes) {
        this.quantidadeHospedes = quantidadeHospedes;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
