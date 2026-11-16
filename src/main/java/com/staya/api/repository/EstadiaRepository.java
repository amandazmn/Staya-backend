package com.staya.api.repository;

import com.staya.api.model.Estadia;
import com.staya.api.model.StatusEstadia;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EstadiaRepository extends JpaRepository<Estadia, Long> {

    @Override
    @EntityGraph(attributePaths = {"reserva", "reserva.cliente", "reserva.quarto", "acompanhantes"})
    Optional<Estadia> findById(Long id);

    /** Filtros opcionais (null = sem filtro); mais recentes primeiro. */
    @EntityGraph(attributePaths = {"reserva", "reserva.cliente", "reserva.quarto", "acompanhantes"})
    @Query("""
            select e from Estadia e
            where (:status is null or e.status = :status)
              and (:quartoId is null or e.reserva.quarto.id = :quartoId)
              and (:clienteId is null or e.reserva.cliente.id = :clienteId)
            order by e.checkinEm desc""")
    List<Estadia> buscar(@Param("status") StatusEstadia status,
                         @Param("quartoId") Long quartoId,
                         @Param("clienteId") Long clienteId);

    /** Estadias em andamento com saída prevista na data. */
    @EntityGraph(attributePaths = {"reserva", "reserva.cliente", "reserva.quarto", "acompanhantes"})
    @Query("""
            select e from Estadia e
            where e.status = com.staya.api.model.StatusEstadia.EM_ANDAMENTO and e.reserva.dataSaida = :data
            order by e.reserva.quarto.numero""")
    List<Estadia> buscarSaidasPrevistas(@Param("data") LocalDate data);

    /** Estadias em andamento cuja saída prevista já passou (hóspede além do período). */
    @EntityGraph(attributePaths = {"reserva", "reserva.cliente", "reserva.quarto", "acompanhantes"})
    @Query("""
            select e from Estadia e
            where e.status = com.staya.api.model.StatusEstadia.EM_ANDAMENTO and e.reserva.dataSaida < :data
            order by e.reserva.dataSaida, e.reserva.quarto.numero""")
    List<Estadia> buscarSaidasAtrasadas(@Param("data") LocalDate data);

    @EntityGraph(attributePaths = {"reserva", "reserva.cliente", "reserva.quarto", "acompanhantes"})
    @Query("""
            select e from Estadia e
            where e.checkinEm >= :inicio and e.checkinEm < :fim
            order by e.checkinEm""")
    List<Estadia> buscarCheckinsEntre(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @EntityGraph(attributePaths = {"reserva", "reserva.cliente", "reserva.quarto", "acompanhantes"})
    @Query("""
            select e from Estadia e
            where e.checkoutEm >= :inicio and e.checkoutEm < :fim
            order by e.checkoutEm""")
    List<Estadia> buscarCheckoutsEntre(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    /** Total de pessoas hospedadas agora (titulares + acompanhantes). */
    @Query("""
            select coalesce(sum(e.reserva.quantidadeHospedes), 0) from Estadia e
            where e.status = com.staya.api.model.StatusEstadia.EM_ANDAMENTO""")
    long contarHospedesNoHotel();

    boolean existsByReservaId(Long reservaId);
}
