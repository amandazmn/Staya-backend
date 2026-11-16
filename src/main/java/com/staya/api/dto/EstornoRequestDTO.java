package com.staya.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EstornoRequestDTO {

    @NotBlank(message = "Motivo do estorno é obrigatório")
    @Size(max = 255)
    private String motivo;

    public EstornoRequestDTO() {}

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
