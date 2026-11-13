package com.staya.api.service;

import com.staya.api.dto.ReservaRequestDTO;
import com.staya.api.dto.ReservaResponseDTO;
import com.staya.api.exception.ConflitoException;
import com.staya.api.exception.RecursoNaoEncontradoException;
import com.staya.api.exception.RegraNegocioException;
import com.staya.api.mapper.ReservaMapper;
import com.staya.api.mapper.Textos;
import com.staya.api.model.Quarto;
import com.staya.api.model.Reserva;
import com.staya.api.model.StatusQuarto;
import com.staya.api.model.StatusReserva;
import com.staya.api.repository.QuartoRepository;
import com.staya.api.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final QuartoRepository quartoRepository;
    private final ClienteService clienteService;

    public ReservaService(ReservaRepository reservaRepository, QuartoRepository quartoRepository,
                          ClienteService clienteService) {
        this.reservaRepository = reservaRepository;
        this.quartoRepository = quartoRepository;
        this.clienteService = clienteService;
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listar(Long clienteId, Long quartoId, StatusReserva status) {
        return reservaRepository.buscar(clienteId, quartoId, status).stream().map(ReservaMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponseDTO buscar(Long id) {
        return ReservaMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ReservaResponseDTO criar(ReservaRequestDTO dto) {
        Reserva reserva = new Reserva();
        preencherEValidar(dto, reserva, null);
        return ReservaMapper.toResponse(reservaRepository.save(reserva));
    }

    /** Altera período, quarto, hóspedes ou observações de uma reserva ainda ativa; o valor é recalculado. */
    @Transactional
    public ReservaResponseDTO atualizar(Long id, ReservaRequestDTO dto) {
        Reserva reserva = buscarEntidade(id);
        exigirAtiva(reserva, "alterada");
        preencherEValidar(dto, reserva, id);
        return ReservaMapper.toResponse(reserva);
    }

    @Transactional
    public ReservaResponseDTO confirmar(Long id) {
        Reserva reserva = buscarEntidade(id);
        if (reserva.getStatus() != StatusReserva.PENDENTE) {
            throw new ConflitoException("Apenas reservas pendentes podem ser confirmadas (status atual: "
                    + reserva.getStatus() + ")");
        }
        reserva.setStatus(StatusReserva.CONFIRMADA);
        return ReservaMapper.toResponse(reserva);
    }

    @Transactional
    public ReservaResponseDTO cancelar(Long id) {
        Reserva reserva = buscarEntidade(id);
        exigirAtiva(reserva, "cancelada");
        reserva.setStatus(StatusReserva.CANCELADA);
        return ReservaMapper.toResponse(reserva);
    }

    private void preencherEValidar(ReservaRequestDTO dto, Reserva reserva, Long idAtual) {
        // Bloqueia o quarto antes de checar conflitos: reservas concorrentes do mesmo quarto esperam aqui.
        Quarto quarto = quartoRepository.findByIdComBloqueio(dto.getQuartoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Quarto", dto.getQuartoId()));

        if (quarto.getStatus() == StatusQuarto.BLOQUEADO) {
            throw new RegraNegocioException("Quarto " + quarto.getNumero() + " está bloqueado para reservas");
        }
        int capacidade = quarto.getTipoQuarto().getCapacidade();
        if (dto.getQuantidadeHospedes() > capacidade) {
            throw new RegraNegocioException("Quarto " + quarto.getNumero() + " comporta no máximo "
                    + capacidade + " hóspede(s)");
        }
        if (reservaRepository.existeConflito(quarto.getId(), dto.getDataEntrada(), dto.getDataSaida(),
                StatusReserva.ATIVOS, idAtual)) {
            throw new ConflitoException("Quarto " + quarto.getNumero() + " já possui reserva no período informado");
        }

        reserva.setCliente(clienteService.buscarEntidade(dto.getClienteId()));
        reserva.setQuarto(quarto);
        reserva.setDataEntrada(dto.getDataEntrada());
        reserva.setDataSaida(dto.getDataSaida());
        reserva.setQuantidadeHospedes(dto.getQuantidadeHospedes());
        reserva.setObservacoes(Textos.limpar(dto.getObservacoes()));
        reserva.aplicarTarifa(quarto.getTipoQuarto().getTarifaBase());
    }

    private void exigirAtiva(Reserva reserva, String acao) {
        if (!reserva.getStatus().isAtiva()) {
            throw new ConflitoException("Reserva com status " + reserva.getStatus() + " não pode ser " + acao);
        }
    }

    private Reserva buscarEntidade(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva", id));
    }
}
