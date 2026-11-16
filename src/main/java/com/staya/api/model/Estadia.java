package com.staya.api.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Período em que o hóspede está de fato no hotel, do check-in ao check-out.
 * Titular, quarto, tarifa e período previsto vêm da reserva.
 */
@Entity
@Table(name = "estadias")
public class Estadia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estadia")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_reserva", nullable = false, unique = true)
    private Reserva reserva;

    @Column(name = "checkin_em", nullable = false)
    private LocalDateTime checkinEm;

    @Column(name = "checkout_em")
    private LocalDateTime checkoutEm;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status", length = 20, nullable = false)
    private StatusEstadia status = StatusEstadia.EM_ANDAMENTO;

    @ManyToMany
    @JoinTable(name = "estadia_acompanhantes",
            joinColumns = @JoinColumn(name = "fk_estadia"),
            inverseJoinColumns = @JoinColumn(name = "fk_cliente"))
    private Set<Cliente> acompanhantes = new LinkedHashSet<>();

    @Column(name = "observacoes", length = 255)
    private String observacoes;

    public Estadia() {}

    /** Titular + acompanhantes. */
    public int getQuantidadeHospedes() {
        return 1 + acompanhantes.size();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public LocalDateTime getCheckinEm() {
        return checkinEm;
    }

    public void setCheckinEm(LocalDateTime checkinEm) {
        this.checkinEm = checkinEm;
    }

    public LocalDateTime getCheckoutEm() {
        return checkoutEm;
    }

    public void setCheckoutEm(LocalDateTime checkoutEm) {
        this.checkoutEm = checkoutEm;
    }

    public StatusEstadia getStatus() {
        return status;
    }

    public void setStatus(StatusEstadia status) {
        this.status = status;
    }

    public Set<Cliente> getAcompanhantes() {
        return acompanhantes;
    }

    public void setAcompanhantes(Set<Cliente> acompanhantes) {
        this.acompanhantes = acompanhantes;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
