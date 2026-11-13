package com.staya.api.mapper;

import com.staya.api.dto.TipoQuartoRequestDTO;
import com.staya.api.dto.TipoQuartoResponseDTO;
import com.staya.api.model.TipoQuarto;

public final class TipoQuartoMapper {

    private TipoQuartoMapper() {}

    public static void copiar(TipoQuartoRequestDTO dto, TipoQuarto tipo) {
        tipo.setNome(dto.getNome().trim());
        tipo.setDescricao(Textos.limpar(dto.getDescricao()));
        tipo.setCapacidade(dto.getCapacidade());
        tipo.setTarifaBase(dto.getTarifaBase());
    }

    public static TipoQuartoResponseDTO toResponse(TipoQuarto tipo) {
        TipoQuartoResponseDTO dto = new TipoQuartoResponseDTO();
        dto.setId(tipo.getId());
        dto.setNome(tipo.getNome());
        dto.setDescricao(tipo.getDescricao());
        dto.setCapacidade(tipo.getCapacidade());
        dto.setTarifaBase(tipo.getTarifaBase());
        return dto;
    }
}
