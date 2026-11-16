package com.staya.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Check-in de hóspede sem reserva (walk-in): a reserva é criada automaticamente, com entrada hoje. */
public class CheckinDiretoRequestDTO {

    @NotNull(message = "Cliente é obrigatório")
    private Long clienteId;

    @NotNull(message = "Quarto é obrigatório")
    private Long quartoId;

    @NotNull(message = "Data de saída é obrigatória")
    private LocalDate dataSaida;

    private List<Long> acompanhanteIds = new ArrayList<>();

    @Size(max = 255)
    private String observacoes;

    public CheckinDiretoRequestDTO() {}

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

    public LocalDate getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDate dataSaida) {
        this.dataSaida = dataSaida;
    }

    public List<Long> getAcompanhanteIds() {
        return acompanhanteIds;
    }

    public void setAcompanhanteIds(List<Long> acompanhanteIds) {
        this.acompanhanteIds = acompanhanteIds == null ? new ArrayList<>() : acompanhanteIds;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
