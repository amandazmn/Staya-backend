package com.staya.api.dto;

import java.math.BigDecimal;

/**
 * Ocupação da noite da data. Vendáveis = total de quartos menos os BLOQUEADO.
 * Taxa = ocupados / vendáveis, em percentual com 1 casa decimal.
 */
public class OcupacaoDTO {

    private Long quartosOcupados;
    private Long quartosVendaveis;
    private BigDecimal taxaPercentual;

    public OcupacaoDTO() {}

    public Long getQuartosOcupados() {
        return quartosOcupados;
    }

    public void setQuartosOcupados(Long quartosOcupados) {
        this.quartosOcupados = quartosOcupados;
    }

    public Long getQuartosVendaveis() {
        return quartosVendaveis;
    }

    public void setQuartosVendaveis(Long quartosVendaveis) {
        this.quartosVendaveis = quartosVendaveis;
    }

    public BigDecimal getTaxaPercentual() {
        return taxaPercentual;
    }

    public void setTaxaPercentual(BigDecimal taxaPercentual) {
        this.taxaPercentual = taxaPercentual;
    }
}
