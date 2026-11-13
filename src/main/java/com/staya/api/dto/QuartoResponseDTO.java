package com.staya.api.dto;

import com.staya.api.model.StatusQuarto;

public class QuartoResponseDTO {

    private Long id;
    private String numero;
    private Integer andar;
    private StatusQuarto status;
    private String observacoes;
    private TipoQuartoResponseDTO tipoQuarto;

    public QuartoResponseDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public StatusQuarto getStatus() {
        return status;
    }

    public void setStatus(StatusQuarto status) {
        this.status = status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public TipoQuartoResponseDTO getTipoQuarto() {
        return tipoQuarto;
    }

    public void setTipoQuarto(TipoQuartoResponseDTO tipoQuarto) {
        this.tipoQuarto = tipoQuarto;
    }
}
