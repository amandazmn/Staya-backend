package com.staya.api.mapper;

import com.staya.api.dto.ConsumoResponseDTO;
import com.staya.api.dto.PagamentoResponseDTO;
import com.staya.api.model.Consumo;
import com.staya.api.model.Pagamento;

public final class ContaMapper {

    private ContaMapper() {}

    public static ConsumoResponseDTO toResponse(Consumo consumo) {
        ConsumoResponseDTO dto = new ConsumoResponseDTO();
        dto.setId(consumo.getId());
        dto.setProdutoId(consumo.getProduto().getId());
        dto.setDescricao(consumo.getDescricao());
        dto.setCategoria(consumo.getProduto().getCategoria());
        dto.setQuantidade(consumo.getQuantidade());
        dto.setValorUnitario(consumo.getValorUnitario());
        dto.setValorTotal(consumo.getValorTotal());
        dto.setLancadoEm(consumo.getLancadoEm());
        dto.setObservacoes(consumo.getObservacoes());
        dto.setEstornado(consumo.isEstornado());
        dto.setEstornadoEm(consumo.getEstornadoEm());
        dto.setMotivoEstorno(consumo.getMotivoEstorno());
        return dto;
    }

    public static PagamentoResponseDTO toResponse(Pagamento pagamento) {
        PagamentoResponseDTO dto = new PagamentoResponseDTO();
        dto.setId(pagamento.getId());
        dto.setForma(pagamento.getForma());
        dto.setValor(pagamento.getValor());
        dto.setPagoEm(pagamento.getPagoEm());
        dto.setObservacoes(pagamento.getObservacoes());
        dto.setEstornado(pagamento.isEstornado());
        dto.setEstornadoEm(pagamento.getEstornadoEm());
        dto.setMotivoEstorno(pagamento.getMotivoEstorno());
        return dto;
    }
}
