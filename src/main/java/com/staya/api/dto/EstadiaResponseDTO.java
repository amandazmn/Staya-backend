package com.staya.api.dto;

import com.staya.api.model.StatusEstadia;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Estadia com os dados da reserva. Enquanto em andamento, {@code dataSaida}, diárias e valor são os previstos;
 * após o check-out, refletem o período efetivamente cobrado.
 */
public class EstadiaResponseDTO {

    private Long id;
    private Long reservaId;
    private StatusEstadia status;
    private HospedeResumoDTO titular;
    private List<HospedeResumoDTO> acompanhantes;
    private Integer quantidadeHospedes;
    private Long quartoId;
    private String quartoNumero;
    private LocalDateTime checkinEm;
    private LocalDateTime checkoutEm;
    private LocalDate dataEntrada;
    private LocalDate dataSaida;
    private Long quantidadeDiarias;
    private BigDecimal valorDiaria;
    private BigDecimal valorHospedagem;
    private String observacoes;

    public EstadiaResponseDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReservaId() {
        return reservaId;
    }

    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }

    public StatusEstadia getStatus() {
        return status;
    }

    public void setStatus(StatusEstadia status) {
        this.status = status;
    }

    public HospedeResumoDTO getTitular() {
        return titular;
    }

    public void setTitular(HospedeResumoDTO titular) {
        this.titular = titular;
    }

    public List<HospedeResumoDTO> getAcompanhantes() {
        return acompanhantes;
    }

    public void setAcompanhantes(List<HospedeResumoDTO> acompanhantes) {
        this.acompanhantes = acompanhantes;
    }

    public Integer getQuantidadeHospedes() {
        return quantidadeHospedes;
    }

    public void setQuantidadeHospedes(Integer quantidadeHospedes) {
        this.quantidadeHospedes = quantidadeHospedes;
    }

    public Long getQuartoId() {
        return quartoId;
    }

    public void setQuartoId(Long quartoId) {
        this.quartoId = quartoId;
    }

    public String getQuartoNumero() {
        return quartoNumero;
    }

    public void setQuartoNumero(String quartoNumero) {
        this.quartoNumero = quartoNumero;
    }

    public LocalDateTime getCheckinEm() {
        return checkinEm;
    }

    public void setCheckinEm(LocalDateTime checkinEm) {
        this.checkinEm = checkinEm;
    }

    public LocalDateTime getCheckoutEm() {
        return checkoutEm;
    }

    public void setCheckoutEm(LocalDateTime checkoutEm) {
        this.checkoutEm = checkoutEm;
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

    public Long getQuantidadeDiarias() {
        return quantidadeDiarias;
    }

    public void setQuantidadeDiarias(Long quantidadeDiarias) {
        this.quantidadeDiarias = quantidadeDiarias;
    }

    public BigDecimal getValorDiaria() {
        return valorDiaria;
    }

    public void setValorDiaria(BigDecimal valorDiaria) {
        this.valorDiaria = valorDiaria;
    }

    public BigDecimal getValorHospedagem() {
        return valorHospedagem;
    }

    public void setValorHospedagem(BigDecimal valorHospedagem) {
        this.valorHospedagem = valorHospedagem;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
