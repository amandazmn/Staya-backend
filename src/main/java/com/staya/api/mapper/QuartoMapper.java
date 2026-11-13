package com.staya.api.mapper;

import com.staya.api.dto.QuartoRequestDTO;
import com.staya.api.dto.QuartoResponseDTO;
import com.staya.api.model.Quarto;

public final class QuartoMapper {

    private QuartoMapper() {}

    /** Copia os dados cadastrais; o tipo de quarto é resolvido pelo service. */
    public static void copiar(QuartoRequestDTO dto, Quarto quarto) {
        quarto.setNumero(dto.getNumero().trim());
        quarto.setAndar(dto.getAndar());
        quarto.setObservacoes(Textos.limpar(dto.getObservacoes()));
    }

    public static QuartoResponseDTO toResponse(Quarto quarto) {
        QuartoResponseDTO dto = new QuartoResponseDTO();
        dto.setId(quarto.getId());
        dto.setNumero(quarto.getNumero());
        dto.setAndar(quarto.getAndar());
        dto.setStatus(quarto.getStatus());
        dto.setObservacoes(quarto.getObservacoes());
        dto.setTipoQuarto(TipoQuartoMapper.toResponse(quarto.getTipoQuarto()));
        return dto;
    }
}
