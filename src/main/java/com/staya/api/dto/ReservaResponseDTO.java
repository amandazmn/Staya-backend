package com.staya.api.dto;

import com.staya.api.model.StatusReserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservaResponseDTO {

    private Long id;
    private Long clienteId;
    private String clienteNome;
    private Long quartoId;
    private String quartoNumero;
    private String tipoQuarto;
    private LocalDate dataEntrada;
    private LocalDate dataSaida;
    private Long quantidadeDiarias;
    private Integer quantidadeHospedes;
    private StatusReserva status;
    private BigDecimal valorDiaria;
    private BigDecimal valorTotal;
    private String observacoes;
    private LocalDateTime criadoEm;

    public ReservaResponseDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
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

    public String getTipoQuarto() {
        return tipoQuarto;
    }

    public void setTipoQuarto(String tipoQuarto) {
        this.tipoQuarto = tipoQuarto;
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

    public Integer getQuantidadeHospedes() {
        return quantidadeHospedes;
    }

    public void setQuantidadeHospedes(Integer quantidadeHospedes) {
        this.quantidadeHospedes = quantidadeHospedes;
    }

    public StatusReserva getStatus() {
        return status;
    }

    public void setStatus(StatusReserva status) {
        this.status = status;
    }

    public BigDecimal getValorDiaria() {
        return valorDiaria;
    }

    public void setValorDiaria(BigDecimal valorDiaria) {
        this.valorDiaria = valorDiaria;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
