package com.staya.api.repository;

import com.staya.api.model.Reserva;
import com.staya.api.model.StatusReserva;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Override
    @EntityGraph(attributePaths = {"cliente", "quarto", "quarto.tipoQuarto"})
    Optional<Reserva> findById(Long id);

    /** Lista reservas com filtros opcionais (parâmetro null = sem filtro), da entrada mais próxima para a mais distante. */
    @EntityGraph(attributePaths = {"cliente", "quarto", "quarto.tipoQuarto"})
    @Query("""
            select r from Reserva r
            where (:clienteId is null or r.cliente.id = :clienteId)
              and (:quartoId is null or r.quarto.id = :quartoId)
              and (:status is null or r.status = :status)
            order by r.dataEntrada, r.id""")
    List<Reserva> buscar(@Param("clienteId") Long clienteId,
                         @Param("quartoId") Long quartoId,
                         @Param("status") StatusReserva status);

    /**
     * Há conflito quando os períodos se sobrepõem: a reserva existente começa antes da nova saída
     * e termina depois da nova entrada. Saída e entrada no mesmo dia não conflitam.
     * {@code ignorarId} exclui a própria reserva ao editar (use null na criação).
     */
    @Query("""
            select count(r) > 0 from Reserva r
            where r.quarto.id = :quartoId
              and r.status in :status
              and r.dataEntrada < :saida
              and r.dataSaida > :entrada
              and (:ignorarId is null or r.id <> :ignorarId)""")
    boolean existeConflito(@Param("quartoId") Long quartoId,
                           @Param("entrada") LocalDate entrada,
                           @Param("saida") LocalDate saida,
                           @Param("status") Collection<StatusReserva> status,
                           @Param("ignorarId") Long ignorarId);

    boolean existsByClienteId(Long clienteId);

    boolean existsByQuartoId(Long quartoId);
}
