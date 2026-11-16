package com.staya.api.mapper;

import com.staya.api.dto.EstadiaResponseDTO;
import com.staya.api.dto.HospedeResumoDTO;
import com.staya.api.model.Cliente;
import com.staya.api.model.Estadia;
import com.staya.api.model.Reserva;

public final class EstadiaMapper {

    private EstadiaMapper() {}

    public static EstadiaResponseDTO toResponse(Estadia estadia) {
        Reserva reserva = estadia.getReserva();
        EstadiaResponseDTO dto = new EstadiaResponseDTO();
        dto.setId(estadia.getId());
        dto.setReservaId(reserva.getId());
        dto.setStatus(estadia.getStatus());
        dto.setTitular(resumo(reserva.getCliente()));
        dto.setAcompanhantes(estadia.getAcompanhantes().stream().map(EstadiaMapper::resumo).toList());
        dto.setQuantidadeHospedes(estadia.getQuantidadeHospedes());
        dto.setQuartoId(reserva.getQuarto().getId());
        dto.setQuartoNumero(reserva.getQuarto().getNumero());
        dto.setCheckinEm(estadia.getCheckinEm());
        dto.setCheckoutEm(estadia.getCheckoutEm());
        dto.setDataEntrada(reserva.getDataEntrada());
        dto.setDataSaida(reserva.getDataSaida());
        dto.setQuantidadeDiarias(reserva.getQuantidadeDiarias());
        dto.setValorDiaria(reserva.getValorDiaria());
        dto.setValorHospedagem(reserva.getValorTotal());
        dto.setObservacoes(estadia.getObservacoes());
        return dto;
    }

    private static HospedeResumoDTO resumo(Cliente cliente) {
        return new HospedeResumoDTO(cliente.getId(), cliente.getNome() + " " + cliente.getSobrenome());
    }
}
