package com.staya.api.service;

import com.staya.api.dto.EstadiaResponseDTO;
import com.staya.api.dto.OcupacaoDTO;
import com.staya.api.dto.PainelDiaDTO;
import com.staya.api.dto.ReservaResponseDTO;
import com.staya.api.mapper.EstadiaMapper;
import com.staya.api.mapper.ReservaMapper;
import com.staya.api.model.Estadia;
import com.staya.api.model.Reserva;
import com.staya.api.model.StatusQuarto;
import com.staya.api.model.StatusReserva;
import com.staya.api.repository.EstadiaRepository;
import com.staya.api.repository.QuartoRepository;
import com.staya.api.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PainelService {

    /** Reservas que ocupam o quarto na noite: tudo que não foi cancelado. */
    private static final Set<StatusReserva> OCUPAM_QUARTO = EnumSet.of(
            StatusReserva.PENDENTE, StatusReserva.CONFIRMADA, StatusReserva.EM_HOSPEDAGEM, StatusReserva.CONCLUIDA);

    private final ReservaRepository reservaRepository;
    private final EstadiaRepository estadiaRepository;
    private final QuartoRepository quartoRepository;
    private final Clock clock;

    public PainelService(ReservaRepository reservaRepository, EstadiaRepository estadiaRepository,
                         QuartoRepository quartoRepository, Clock clock) {
        this.reservaRepository = reservaRepository;
        this.estadiaRepository = estadiaRepository;
        this.quartoRepository = quartoRepository;
        this.clock = clock;
    }

    /** Painel da data informada; sem data, usa o dia de hoje no fuso do hotel. */
    @Transactional(readOnly = true)
    public PainelDiaDTO painel(LocalDate data) {
        LocalDate dia = data != null ? data : LocalDate.now(clock);
        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fim = dia.plusDays(1).atStartOfDay();

        PainelDiaDTO painel = new PainelDiaDTO();
        painel.setData(dia);
        painel.setChegadasPrevistas(reservas(reservaRepository.buscarPorEntrada(dia, StatusReserva.EDITAVEIS)));
        painel.setChegadasRealizadas(estadias(estadiaRepository.buscarCheckinsEntre(inicio, fim)));
        painel.setChegadasAtrasadas(reservas(reservaRepository.buscarComEntradaAntesDe(dia, StatusReserva.EDITAVEIS)));
        painel.setSaidasPrevistas(estadias(estadiaRepository.buscarSaidasPrevistas(dia)));
        painel.setSaidasRealizadas(estadias(estadiaRepository.buscarCheckoutsEntre(inicio, fim)));
        painel.setSaidasAtrasadas(estadias(estadiaRepository.buscarSaidasAtrasadas(dia)));

        Map<StatusQuarto, Long> porStatus = contarQuartosPorStatus();
        painel.setQuartosPorStatus(porStatus);
        painel.setOcupacao(ocupacao(dia, porStatus));
        painel.setHospedesNoHotel(estadiaRepository.contarHospedesNoHotel());
        return painel;
    }

    private Map<StatusQuarto, Long> contarQuartosPorStatus() {
        Map<StatusQuarto, Long> porStatus = new EnumMap<>(StatusQuarto.class);
        for (StatusQuarto status : StatusQuarto.values()) {
            porStatus.put(status, 0L);
        }
        for (Object[] linha : quartoRepository.contarPorStatus()) {
            porStatus.put((StatusQuarto) linha[0], (Long) linha[1]);
        }
        return porStatus;
    }

    private OcupacaoDTO ocupacao(LocalDate dia, Map<StatusQuarto, Long> porStatus) {
        long total = porStatus.values().stream().mapToLong(Long::longValue).sum();
        long vendaveis = total - porStatus.get(StatusQuarto.BLOQUEADO);
        long ocupados = reservaRepository.contarQuartosOcupados(dia, OCUPAM_QUARTO);

        OcupacaoDTO ocupacao = new OcupacaoDTO();
        ocupacao.setQuartosOcupados(ocupados);
        ocupacao.setQuartosVendaveis(vendaveis);
        ocupacao.setTaxaPercentual(vendaveis == 0 ? BigDecimal.ZERO.setScale(1)
                : BigDecimal.valueOf(ocupados * 100).divide(BigDecimal.valueOf(vendaveis), 1, RoundingMode.HALF_UP));
        return ocupacao;
    }

    private static List<ReservaResponseDTO> reservas(List<Reserva> reservas) {
        return reservas.stream().map(ReservaMapper::toResponse).toList();
    }

    private static List<EstadiaResponseDTO> estadias(List<Estadia> estadias) {
        return estadias.stream().map(EstadiaMapper::toResponse).toList();
    }
}
