package com.staya.api.dto;

import com.staya.api.model.StatusQuarto;
import jakarta.validation.constraints.NotNull;

public class QuartoStatusDTO {

    @NotNull(message = "Status é obrigatório")
    private StatusQuarto status;

    public QuartoStatusDTO() {}

    public StatusQuarto getStatus() {
        return status;
    }

    public void setStatus(StatusQuarto status) {
        this.status = status;
    }
}
