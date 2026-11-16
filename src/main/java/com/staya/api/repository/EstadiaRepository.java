package com.staya.api.repository;

import com.staya.api.model.Estadia;
import com.staya.api.model.StatusEstadia;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    boolean existsByReservaId(Long reservaId);
}
