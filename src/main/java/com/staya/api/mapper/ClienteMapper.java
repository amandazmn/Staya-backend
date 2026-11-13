package com.staya.api.mapper;

import com.staya.api.dto.ClienteRequestDTO;
import com.staya.api.dto.ClienteResponseDTO;
import com.staya.api.model.Cliente;

public final class ClienteMapper {

    private ClienteMapper() {}

    /** Copia os dados simples do DTO; o endereço é resolvido pelo service. */
    public static void copiar(ClienteRequestDTO dto, Cliente cliente) {
        cliente.setNome(dto.getNome().trim());
        cliente.setSobrenome(dto.getSobrenome().trim());
        cliente.setCpf(Textos.limpar(dto.getCpf()));
        cliente.setPassaporte(Textos.limpar(dto.getPassaporte()));
        cliente.setEmail(Textos.limpar(dto.getEmail()));
        cliente.setTelefone(Textos.limpar(dto.getTelefone()));
        cliente.setNacionalidade(dto.getNacionalidade().trim());
    }

    public static ClienteResponseDTO toResponse(Cliente cliente) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setSobrenome(cliente.getSobrenome());
        dto.setCpf(cliente.getCpf());
        dto.setPassaporte(cliente.getPassaporte());
        dto.setEmail(cliente.getEmail());
        dto.setTelefone(cliente.getTelefone());
        dto.setNacionalidade(cliente.getNacionalidade());
        dto.setEndereco(EnderecoMapper.toResponse(cliente.getEndereco()));
        return dto;
    }
}
