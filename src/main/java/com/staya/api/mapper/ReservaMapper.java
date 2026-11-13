package com.staya.api.mapper;

import com.staya.api.dto.ReservaResponseDTO;
import com.staya.api.model.Cliente;
import com.staya.api.model.Quarto;
import com.staya.api.model.Reserva;

public final class ReservaMapper {

    private ReservaMapper() {}

    public static ReservaResponseDTO toResponse(Reserva reserva) {
        Cliente cliente = reserva.getCliente();
        Quarto quarto = reserva.getQuarto();
        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setId(reserva.getId());
        dto.setClienteId(cliente.getId());
        dto.setClienteNome(cliente.getNome() + " " + cliente.getSobrenome());
        dto.setQuartoId(quarto.getId());
        dto.setQuartoNumero(quarto.getNumero());
        dto.setTipoQuarto(quarto.getTipoQuarto().getNome());
        dto.setDataEntrada(reserva.getDataEntrada());
        dto.setDataSaida(reserva.getDataSaida());
        dto.setQuantidadeDiarias(reserva.getQuantidadeDiarias());
        dto.setQuantidadeHospedes(reserva.getQuantidadeHospedes());
        dto.setStatus(reserva.getStatus());
        dto.setValorDiaria(reserva.getValorDiaria());
        dto.setValorTotal(reserva.getValorTotal());
        dto.setObservacoes(reserva.getObservacoes());
        dto.setCriadoEm(reserva.getCriadoEm());
        return dto;
    }
}
