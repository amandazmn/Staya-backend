package com.staya.api.mapper;

import com.staya.api.dto.ProdutoRequestDTO;
import com.staya.api.dto.ProdutoResponseDTO;
import com.staya.api.model.Produto;

public final class ProdutoMapper {

    private ProdutoMapper() {}

    public static void copiar(ProdutoRequestDTO dto, Produto produto) {
        produto.setNome(dto.getNome().trim());
        produto.setCategoria(dto.getCategoria());
        produto.setPreco(dto.getPreco());
        produto.setAtivo(dto.getAtivo() == null || dto.getAtivo());
    }

    public static ProdutoResponseDTO toResponse(Produto produto) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setCategoria(produto.getCategoria());
        dto.setPreco(produto.getPreco());
        dto.setAtivo(produto.isAtivo());
        return dto;
    }
}
