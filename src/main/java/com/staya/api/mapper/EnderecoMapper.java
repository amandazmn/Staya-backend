package com.staya.api.mapper;

import com.staya.api.dto.EnderecoRequestDTO;
import com.staya.api.dto.EnderecoResponseDTO;
import com.staya.api.model.Endereco;

public final class EnderecoMapper {

    private EnderecoMapper() {}

    public static void copiar(EnderecoRequestDTO dto, Endereco endereco) {
        endereco.setLogradouro(dto.getLogradouro().trim());
        endereco.setNumero(Textos.limpar(dto.getNumero()));
        endereco.setComplemento(Textos.limpar(dto.getComplemento()));
        endereco.setBairro(Textos.limpar(dto.getBairro()));
        endereco.setCidade(dto.getCidade().trim());
        endereco.setEstado(Textos.limpar(dto.getEstado()));
        endereco.setPais(dto.getPais().trim());
        endereco.setCep(Textos.limpar(dto.getCep()));
    }

    public static EnderecoResponseDTO toResponse(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        EnderecoResponseDTO dto = new EnderecoResponseDTO();
        dto.setId(endereco.getId());
        dto.setLogradouro(endereco.getLogradouro());
        dto.setNumero(endereco.getNumero());
        dto.setComplemento(endereco.getComplemento());
        dto.setBairro(endereco.getBairro());
        dto.setCidade(endereco.getCidade());
        dto.setEstado(endereco.getEstado());
        dto.setPais(endereco.getPais());
        dto.setCep(endereco.getCep());
        return dto;
    }
}
