package com.staya.api.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long id;

    /** Hóspede titular da reserva. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_quarto", nullable = false)
    private Quarto quarto;

    @Column(name = "data_entrada", nullable = false)
    private LocalDate dataEntrada;

    /** Dia do check-out; o quarto fica livre para nova entrada nesse mesmo dia. */
    @Column(name = "data_saida", nullable = false)
    private LocalDate dataSaida;

    @Column(name = "quantidade_hospedes", nullable = false)
    private Integer quantidadeHospedes;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status", length = 20, nullable = false)
    private StatusReserva status = StatusReserva.PENDENTE;

    /** Tarifa congelada no momento da reserva: mudar a tarifa do tipo de quarto não altera reservas existentes. */
    @Column(name = "valor_diaria", precision = 10, scale = 2, nullable = false)
    private BigDecimal valorDiaria;

    @Column(name = "valor_total", precision = 12, scale = 2, nullable = false)
    private BigDecimal valorTotal;

    @Column(name = "observacoes", length = 255)
    private String observacoes;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    public Reserva() {}

    public long getQuantidadeDiarias() {
        return ChronoUnit.DAYS.between(dataEntrada, dataSaida);
    }

    /** Define a diária e recalcula o total (diárias × valor da diária). */
    public void aplicarTarifa(BigDecimal valorDiaria) {
        this.valorDiaria = valorDiaria;
        this.valorTotal = valorDiaria.multiply(BigDecimal.valueOf(getQuantidadeDiarias()));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Quarto getQuarto() {
        return quarto;
    }

    public void setQuarto(Quarto quarto) {
        this.quarto = quarto;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDate getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDate dataSaida) {
        this.dataSaida = dataSaida;
    }

    public Integer getQuantidadeHospedes() {
        return quantidadeHospedes;
    }

    public void setQuantidadeHospedes(Integer quantidadeHospedes) {
        this.quantidadeHospedes = quantidadeHospedes;
    }

    public StatusReserva getStatus() {
        return status;
    }

    public void setStatus(StatusReserva status) {
        this.status = status;
    }

    public BigDecimal getValorDiaria() {
        return valorDiaria;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
