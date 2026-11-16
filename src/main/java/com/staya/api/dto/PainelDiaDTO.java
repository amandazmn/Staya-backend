package com.staya.api.dto;

import com.staya.api.model.StatusQuarto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Visão do dia para a recepção.
 * <ul>
 *   <li>chegadasPrevistas: reservas com entrada na data aguardando check-in;</li>
 *   <li>chegadasRealizadas / saidasRealizadas: check-ins e check-outs feitos na data;</li>
 *   <li>chegadasAtrasadas: entrada antes da data e sem check-in (possível no-show);</li>
 *   <li>saidasPrevistas: hóspedes com saída prevista na data;</li>
 *   <li>saidasAtrasadas: hóspedes que passaram da data de saída sem check-out.</li>
 * </ul>
 * {@code quartosPorStatus} e {@code hospedesNoHotel} mostram a situação atual, independentemente da data.
 */
public class PainelDiaDTO {

    private LocalDate data;
    private List<ReservaResponseDTO> chegadasPrevistas;
    private List<EstadiaResponseDTO> chegadasRealizadas;
    private List<ReservaResponseDTO> chegadasAtrasadas;
    private List<EstadiaResponseDTO> saidasPrevistas;
    private List<EstadiaResponseDTO> saidasRealizadas;
    private List<EstadiaResponseDTO> saidasAtrasadas;
    private OcupacaoDTO ocupacao;
    private Map<StatusQuarto, Long> quartosPorStatus;
    private Long hospedesNoHotel;

    public PainelDiaDTO() {}

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public List<ReservaResponseDTO> getChegadasPrevistas() {
        return chegadasPrevistas;
    }

    public void setChegadasPrevistas(List<ReservaResponseDTO> chegadasPrevistas) {
        this.chegadasPrevistas = chegadasPrevistas;
    }

    public List<EstadiaResponseDTO> getChegadasRealizadas() {
        return chegadasRealizadas;
    }

    public void setChegadasRealizadas(List<EstadiaResponseDTO> chegadasRealizadas) {
        this.chegadasRealizadas = chegadasRealizadas;
    }

    public List<ReservaResponseDTO> getChegadasAtrasadas() {
        return chegadasAtrasadas;
    }

    public void setChegadasAtrasadas(List<ReservaResponseDTO> chegadasAtrasadas) {
        this.chegadasAtrasadas = chegadasAtrasadas;
    }

    public List<EstadiaResponseDTO> getSaidasPrevistas() {
        return saidasPrevistas;
    }

    public void setSaidasPrevistas(List<EstadiaResponseDTO> saidasPrevistas) {
        this.saidasPrevistas = saidasPrevistas;
    }

    public List<EstadiaResponseDTO> getSaidasRealizadas() {
        return saidasRealizadas;
    }

    public void setSaidasRealizadas(List<EstadiaResponseDTO> saidasRealizadas) {
        this.saidasRealizadas = saidasRealizadas;
    }

    public List<EstadiaResponseDTO> getSaidasAtrasadas() {
        return saidasAtrasadas;
    }

    public void setSaidasAtrasadas(List<EstadiaResponseDTO> saidasAtrasadas) {
        this.saidasAtrasadas = saidasAtrasadas;
    }

    public OcupacaoDTO getOcupacao() {
        return ocupacao;
    }

    public void setOcupacao(OcupacaoDTO ocupacao) {
        this.ocupacao = ocupacao;
    }

    public Map<StatusQuarto, Long> getQuartosPorStatus() {
        return quartosPorStatus;
    }

    public void setQuartosPorStatus(Map<StatusQuarto, Long> quartosPorStatus) {
        this.quartosPorStatus = quartosPorStatus;
    }

    public Long getHospedesNoHotel() {
        return hospedesNoHotel;
    }

    public void setHospedesNoHotel(Long hospedesNoHotel) {
        this.hospedesNoHotel = hospedesNoHotel;
    }
}
