package com.staya.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Dados cadastrais do quarto. O status não entra aqui: ele muda pela rota
 * PATCH /quartos/{id}/status (e, futuramente, pelo check-in/check-out).
 */
public class QuartoRequestDTO {

    @NotBlank(message = "Número é obrigatório")
    @Size(max = 10)
    private String numero;

    private Integer andar;

    @NotNull(message = "Tipo de quarto é obrigatório")
    private Long tipoQuartoId;

    @Size(max = 255)
    private String observacoes;

    public QuartoRequestDTO() {}

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Integer getAndar() {
        return andar;
    }

    public void setAndar(Integer andar) {
        this.andar = andar;
    }

    public Long getTipoQuartoId() {
        return tipoQuartoId;
    }

    public void setTipoQuartoId(Long tipoQuartoId) {
        this.tipoQuartoId = tipoQuartoId;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
