package com.staya.api.service;

import com.staya.api.dto.CheckinDiretoRequestDTO;
import com.staya.api.dto.CheckinRequestDTO;
import com.staya.api.dto.EstadiaResponseDTO;
import com.staya.api.dto.ReservaRequestDTO;
import com.staya.api.exception.ConflitoException;
import com.staya.api.exception.RecursoNaoEncontradoException;
import com.staya.api.exception.RegraNegocioException;
import com.staya.api.mapper.EstadiaMapper;
import com.staya.api.mapper.Textos;
import com.staya.api.model.*;
import com.staya.api.repository.EstadiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class EstadiaService {

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final EstadiaRepository estadiaRepository;
    private final ReservaService reservaService;
    private final ClienteService clienteService;
    private final ContaService contaService;
    private final Clock clock;

    public EstadiaService(EstadiaRepository estadiaRepository, ReservaService reservaService,
                          ClienteService clienteService, ContaService contaService, Clock clock) {
        this.estadiaRepository = estadiaRepository;
        this.reservaService = reservaService;
        this.clienteService = clienteService;
        this.contaService = contaService;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<EstadiaResponseDTO> listar(StatusEstadia status, Long quartoId, Long clienteId) {
        return estadiaRepository.buscar(status, quartoId, clienteId).stream().map(EstadiaMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EstadiaResponseDTO buscar(Long id) {
        return EstadiaMapper.toResponse(buscarEntidade(id));
    }

    /** Check-in de uma reserva confirmada, a partir do dia de entrada e antes do dia de saída. */
    @Transactional
    public EstadiaResponseDTO checkin(CheckinRequestDTO dto) {
        Reserva reserva = reservaService.buscarEntidade(dto.getReservaId());
        if (reserva.getStatus() != StatusReserva.CONFIRMADA) {
            throw new ConflitoException("Somente reservas confirmadas podem fazer check-in (status atual: "
                    + reserva.getStatus() + ")");
        }
        LocalDate hoje = LocalDate.now(clock);
        if (hoje.isBefore(reserva.getDataEntrada())) {
            throw new RegraNegocioException("Check-in desta reserva só é permitido a partir de "
                    + reserva.getDataEntrada().format(DATA_BR));
        }
        if (!hoje.isBefore(reserva.getDataSaida())) {
            throw new RegraNegocioException("O período desta reserva terminou em "
                    + reserva.getDataSaida().format(DATA_BR) + "; altere as datas antes do check-in");
        }
        return EstadiaMapper.toResponse(hospedar(reserva, dto.getAcompanhanteIds(), dto.getObservacoes()));
    }

    /** Walk-in: cria a reserva (entrada hoje, já confirmada) e faz o check-in na mesma transação. */
    @Transactional
    public EstadiaResponseDTO checkinDireto(CheckinDiretoRequestDTO dto) {
        Set<Long> acompanhanteIds = idsUnicos(dto.getAcompanhanteIds());
        ReservaRequestDTO reservaDTO = new ReservaRequestDTO();
        reservaDTO.setClienteId(dto.getClienteId());
        reservaDTO.setQuartoId(dto.getQuartoId());
        reservaDTO.setDataEntrada(LocalDate.now(clock));
        reservaDTO.setDataSaida(dto.getDataSaida());
        reservaDTO.setQuantidadeHospedes(1 + acompanhanteIds.size());
        reservaDTO.setObservacoes("Check-in direto (sem reserva prévia)");
        if (!dto.getDataSaida().isAfter(reservaDTO.getDataEntrada())) {
            throw new RegraNegocioException("Data de saída deve ser posterior a hoje");
        }

        Reserva reserva = reservaService.criarEntidade(reservaDTO);
        reserva.setStatus(StatusReserva.CONFIRMADA);
        return EstadiaMapper.toResponse(hospedar(reserva, dto.getAcompanhanteIds(), dto.getObservacoes()));
    }

    /**
     * Encerra a estadia. As diárias passam a ser as efetivamente usadas (do dia de entrada da reserva
     * até hoje, mínimo 1): saída antecipada libera o quarto para novas reservas e reduz o valor.
     * O quarto vai para LIMPEZA e a reserva para CONCLUIDA.
     * Exige a conta quitada (saldo <= 0) já com o valor final da hospedagem.
     */
    @Transactional
    public EstadiaResponseDTO checkout(Long id) {
        Estadia estadia = buscarEntidade(id);
        if (estadia.getStatus() != StatusEstadia.EM_ANDAMENTO) {
            throw new ConflitoException("Esta estadia já foi encerrada");
        }
        LocalDateTime agora = LocalDateTime.now(clock);
        Reserva reserva = estadia.getReserva();

        long diarias = Math.max(1, ChronoUnit.DAYS.between(reserva.getDataEntrada(), agora.toLocalDate()));
        // Saldo calculado antes de alterar qualquer dado: se houver pendência, nada muda
        BigDecimal valorHospedagem = reserva.getValorDiaria().multiply(BigDecimal.valueOf(diarias));
        BigDecimal saldo = contaService.saldoCom(estadia, valorHospedagem);
        if (saldo.signum() > 0) {
            throw new RegraNegocioException("Conta com saldo pendente de " + ContaService.formatarReais(saldo)
                    + "; registre o pagamento antes do check-out");
        }

        reserva.setDataSaida(reserva.getDataEntrada().plusDays(diarias));
        reserva.aplicarTarifa(reserva.getValorDiaria());
        reserva.setStatus(StatusReserva.CONCLUIDA);

        reserva.getQuarto().setStatus(StatusQuarto.LIMPEZA);

        estadia.setCheckoutEm(agora);
        estadia.setStatus(StatusEstadia.ENCERRADA);
        return EstadiaMapper.toResponse(estadia);
    }

    private Estadia hospedar(Reserva reserva, List<Long> acompanhanteIds, String observacoes) {
        Quarto quarto = reserva.getQuarto();
        if (quarto.getStatus() != StatusQuarto.DISPONIVEL) {
            throw new RegraNegocioException("Quarto " + quarto.getNumero()
                    + " não está pronto para receber hóspedes (status: " + quarto.getStatus() + ")");
        }

        Set<Cliente> acompanhantes = new LinkedHashSet<>();
        for (Long clienteId : idsUnicos(acompanhanteIds)) {
            if (clienteId.equals(reserva.getCliente().getId())) {
                throw new RegraNegocioException("O titular da reserva não deve ser informado como acompanhante");
            }
            acompanhantes.add(clienteService.buscarEntidade(clienteId));
        }
        int hospedes = 1 + acompanhantes.size();
        int capacidade = quarto.getTipoQuarto().getCapacidade();
        if (hospedes > capacidade) {
            throw new RegraNegocioException("Quarto " + quarto.getNumero() + " comporta no máximo "
                    + capacidade + " hóspede(s)");
        }
        if (estadiaRepository.existsByReservaId(reserva.getId())) {
            throw new ConflitoException("Esta reserva já possui check-in");
        }

        Estadia estadia = new Estadia();
        estadia.setReserva(reserva);
        estadia.setCheckinEm(LocalDateTime.now(clock));
        estadia.setAcompanhantes(acompanhantes);
        estadia.setObservacoes(Textos.limpar(observacoes));

        reserva.setQuantidadeHospedes(hospedes);
        reserva.setStatus(StatusReserva.EM_HOSPEDAGEM);
        quarto.setStatus(StatusQuarto.OCUPADO);
        return estadiaRepository.save(estadia);
    }

    private static Set<Long> idsUnicos(List<Long> ids) {
        Set<Long> unicos = new LinkedHashSet<>();
        ids.stream().filter(Objects::nonNull).forEach(unicos::add);
        return unicos;
    }

    private Estadia buscarEntidade(Long id) {
        return estadiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estadia", id));
    }
}
