package com.staya.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/** Check-in de uma reserva existente (que precisa estar CONFIRMADA). */
public class CheckinRequestDTO {

    @NotNull(message = "Reserva é obrigatória")
    private Long reservaId;

    /** Clientes que se hospedam junto com o titular (precisam estar cadastrados). */
    private List<Long> acompanhanteIds = new ArrayList<>();

    @Size(max = 255)
    private String observacoes;

    public CheckinRequestDTO() {}

    public Long getReservaId() {
        return reservaId;
    }

    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
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
