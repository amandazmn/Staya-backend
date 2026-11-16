package com.staya.api.dto;

import com.staya.api.model.StatusEstadia;

import java.math.BigDecimal;
import java.util.List;

/**
 * Conta da estadia: hospedagem + consumos - pagamentos. Lançamentos estornados aparecem na lista,
 * mas não entram nos totais. Saldo positivo = valor a receber; negativo = valor a devolver ao hóspede.
 * Enquanto a estadia está em andamento, a hospedagem é a prevista; no check-out passa a ser a efetiva.
 */
public class ContaDTO {

    private Long estadiaId;

    private StatusEstadia statusEstadia;

    private Boolean contaFechada;

    private BigDecimal valorHospedagem;

    private List<ConsumoResponseDTO> consumos;

    private BigDecimal totalConsumos;

    private List<PagamentoResponseDTO> pagamentos;

    private BigDecimal totalPagamentos;

    private BigDecimal totalConta;

    private BigDecimal saldo;

    public ContaDTO() {}

    public Long getEstadiaId() {
        return estadiaId;
    }

    public void setEstadiaId(Long estadiaId) {
        this.estadiaId = estadiaId;
    }

    public StatusEstadia getStatusEstadia() {
        return statusEstadia;
    }

    public void setStatusEstadia(StatusEstadia statusEstadia) {
        this.statusEstadia = statusEstadia;
    }

    public Boolean getContaFechada() {
        return contaFechada;
    }

    public void setContaFechada(Boolean contaFechada) {
        this.contaFechada = contaFechada;
    }

    public BigDecimal getValorHospedagem() {
        return valorHospedagem;
    }

    public void setValorHospedagem(BigDecimal valorHospedagem) {
        this.valorHospedagem = valorHospedagem;
    }

    public List<ConsumoResponseDTO> getConsumos() {
        return consumos;
    }

    public void setConsumos(List<ConsumoResponseDTO> consumos) {
        this.consumos = consumos;
    }

    public BigDecimal getTotalConsumos() {
        return totalConsumos;
    }

    public void setTotalConsumos(BigDecimal totalConsumos) {
        this.totalConsumos = totalConsumos;
    }

    public List<PagamentoResponseDTO> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<PagamentoResponseDTO> pagamentos) {
        this.pagamentos = pagamentos;
    }

    public BigDecimal getTotalPagamentos() {
        return totalPagamentos;
    }

    public void setTotalPagamentos(BigDecimal totalPagamentos) {
        this.totalPagamentos = totalPagamentos;
    }

    public BigDecimal getTotalConta() {
        return totalConta;
    }

    public void setTotalConta(BigDecimal totalConta) {
        this.totalConta = totalConta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
