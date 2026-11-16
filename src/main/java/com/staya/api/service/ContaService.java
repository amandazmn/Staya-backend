package com.staya.api.service;

import com.staya.api.dto.*;
import com.staya.api.exception.ConflitoException;
import com.staya.api.exception.RecursoNaoEncontradoException;
import com.staya.api.exception.RegraNegocioException;
import com.staya.api.mapper.ContaMapper;
import com.staya.api.mapper.Textos;
import com.staya.api.model.*;
import com.staya.api.repository.ConsumoRepository;
import com.staya.api.repository.EstadiaRepository;
import com.staya.api.repository.PagamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * Conta da estadia: consumos e pagamentos. Lançamentos nunca são apagados; correções são feitas por estorno.
 * Depois do check-out a conta fica fechada para novos lançamentos e estornos.
 */
@Service
public class ContaService {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final EstadiaRepository estadiaRepository;
    private final ConsumoRepository consumoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final ProdutoService produtoService;
    private final Clock clock;

    public ContaService(EstadiaRepository estadiaRepository, ConsumoRepository consumoRepository,
                        PagamentoRepository pagamentoRepository, ProdutoService produtoService, Clock clock) {
        this.estadiaRepository = estadiaRepository;
        this.consumoRepository = consumoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.produtoService = produtoService;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public ContaDTO conta(Long estadiaId) {
        Estadia estadia = buscarEstadia(estadiaId);
        List<Consumo> consumos = consumoRepository.findByEstadiaIdOrderByLancadoEmAscIdAsc(estadiaId);
        List<Pagamento> pagamentos = pagamentoRepository.findByEstadiaIdOrderByPagoEmAscIdAsc(estadiaId);

        BigDecimal hospedagem = estadia.getReserva().getValorTotal();
        BigDecimal totalConsumos = somarConsumos(consumos);
        BigDecimal totalPagamentos = somarPagamentos(pagamentos);
        BigDecimal totalConta = hospedagem.add(totalConsumos);

        ContaDTO dto = new ContaDTO();
        dto.setEstadiaId(estadia.getId());
        dto.setStatusEstadia(estadia.getStatus());
        dto.setContaFechada(estadia.getStatus() == StatusEstadia.ENCERRADA);
        dto.setValorHospedagem(hospedagem);
        dto.setConsumos(consumos.stream().map(ContaMapper::toResponse).toList());
        dto.setTotalConsumos(totalConsumos);
        dto.setPagamentos(pagamentos.stream().map(ContaMapper::toResponse).toList());
        dto.setTotalPagamentos(totalPagamentos);
        dto.setTotalConta(totalConta);
        dto.setSaldo(totalConta.subtract(totalPagamentos));
        return dto;
    }

    /** Saldo que a conta teria com o valor de hospedagem informado (usado no check-out, antes de alterar dados). */
    BigDecimal saldoCom(Estadia estadia, BigDecimal valorHospedagem) {
        BigDecimal consumos = somarConsumos(consumoRepository.findByEstadiaIdOrderByLancadoEmAscIdAsc(estadia.getId()));
        BigDecimal pagamentos = somarPagamentos(pagamentoRepository.findByEstadiaIdOrderByPagoEmAscIdAsc(estadia.getId()));
        return valorHospedagem.add(consumos).subtract(pagamentos);
    }

    @Transactional
    public ConsumoResponseDTO lancarConsumo(Long estadiaId, ConsumoRequestDTO dto) {
        Estadia estadia = buscarEstadiaAberta(estadiaId);
        Produto produto = produtoService.buscarEntidade(dto.getProdutoId());
        if (!produto.isAtivo()) {
            throw new RegraNegocioException("Produto '" + produto.getNome() + "' está inativo");
        }

        Consumo consumo = new Consumo();
        consumo.setEstadia(estadia);
        consumo.setProduto(produto);
        consumo.setDescricao(produto.getNome());
        consumo.setQuantidade(dto.getQuantidade());
        consumo.setValorUnitario(produto.getPreco());
        consumo.setValorTotal(produto.getPreco().multiply(BigDecimal.valueOf(dto.getQuantidade())));
        consumo.setLancadoEm(LocalDateTime.now(clock));
        consumo.setObservacoes(Textos.limpar(dto.getObservacoes()));
        return ContaMapper.toResponse(consumoRepository.save(consumo));
    }

    @Transactional
    public ConsumoResponseDTO estornarConsumo(Long estadiaId, Long consumoId, EstornoRequestDTO dto) {
        buscarEstadiaAberta(estadiaId);
        Consumo consumo = consumoRepository.findByIdAndEstadiaId(consumoId, estadiaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consumo", consumoId));
        if (consumo.isEstornado()) {
            throw new ConflitoException("Este consumo já foi estornado");
        }
        consumo.estornar(LocalDateTime.now(clock), dto.getMotivo().trim());
        return ContaMapper.toResponse(consumo);
    }

    /** Pagamentos acima do saldo são aceitos (sinal, adiantamento); o excedente aparece como saldo negativo. */
    @Transactional
    public PagamentoResponseDTO registrarPagamento(Long estadiaId, PagamentoRequestDTO dto) {
        Estadia estadia = buscarEstadiaAberta(estadiaId);
        Pagamento pagamento = new Pagamento();
        pagamento.setEstadia(estadia);
        pagamento.setForma(dto.getForma());
        pagamento.setValor(dto.getValor());
        pagamento.setPagoEm(LocalDateTime.now(clock));
        pagamento.setObservacoes(Textos.limpar(dto.getObservacoes()));
        return ContaMapper.toResponse(pagamentoRepository.save(pagamento));
    }

    @Transactional
    public PagamentoResponseDTO estornarPagamento(Long estadiaId, Long pagamentoId, EstornoRequestDTO dto) {
        buscarEstadiaAberta(estadiaId);
        Pagamento pagamento = pagamentoRepository.findByIdAndEstadiaId(pagamentoId, estadiaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pagamento", pagamentoId));
        if (pagamento.isEstornado()) {
            throw new ConflitoException("Este pagamento já foi estornado");
        }
        pagamento.estornar(LocalDateTime.now(clock), dto.getMotivo().trim());
        return ContaMapper.toResponse(pagamento);
    }

    static String formatarReais(BigDecimal valor) {
        return String.format(PT_BR, "R$ %,.2f", valor);
    }

    private static BigDecimal somarConsumos(List<Consumo> consumos) {
        return consumos.stream().filter(c -> !c.isEstornado())
                .map(Consumo::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal somarPagamentos(List<Pagamento> pagamentos) {
        return pagamentos.stream().filter(p -> !p.isEstornado())
                .map(Pagamento::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Estadia buscarEstadiaAberta(Long estadiaId) {
        Estadia estadia = buscarEstadia(estadiaId);
        if (estadia.getStatus() != StatusEstadia.EM_ANDAMENTO) {
            throw new ConflitoException("A conta desta estadia está fechada (check-out realizado)");
        }
        return estadia;
    }

    private Estadia buscarEstadia(Long id) {
        return estadiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estadia", id));
    }
}
