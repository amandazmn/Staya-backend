package com.staya.api.repository;

import com.staya.api.model.Quarto;
import com.staya.api.model.StatusQuarto;
import com.staya.api.model.StatusReserva;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface QuartoRepository extends JpaRepository<Quarto, Long> {

    @Override
    @EntityGraph(attributePaths = "tipoQuarto")
    Optional<Quarto> findById(Long id);

    /** Lista quartos com filtros opcionais (parâmetro null = sem filtro), ordenados por número. */
    @EntityGraph(attributePaths = "tipoQuarto")
    @Query("""
            select q from Quarto q
            where (:status is null or q.status = :status)
              and (:tipoQuartoId is null or q.tipoQuarto.id = :tipoQuartoId)
              and (:andar is null or q.andar = :andar)
            order by q.numero""")
    List<Quarto> buscar(@Param("status") StatusQuarto status,
                        @Param("tipoQuartoId") Long tipoQuartoId,
                        @Param("andar") Integer andar);

    /**
     * Busca o quarto com bloqueio de escrita (SELECT ... FOR UPDATE). Duas reservas simultâneas
     * para o mesmo quarto ficam em fila, evitando que ambas passem na verificação de conflito.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select q from Quarto q where q.id = :id")
    Optional<Quarto> findByIdComBloqueio(@Param("id") Long id);

    /** Quartos que comportam a quantidade de hóspedes e não têm reserva ativa no período. */
    @EntityGraph(attributePaths = "tipoQuarto")
    @Query("""
            select q from Quarto q
            where q.status <> :excluirStatus
              and q.tipoQuarto.capacidade >= :hospedes
              and not exists (
                  select r.id from Reserva r
                  where r.quarto = q
                    and r.status in :statusAtivos
                    and r.dataEntrada < :saida
                    and r.dataSaida > :entrada)
            order by q.numero""")
    List<Quarto> buscarDisponiveis(@Param("entrada") LocalDate entrada,
                                   @Param("saida") LocalDate saida,
                                   @Param("hospedes") int hospedes,
                                   @Param("excluirStatus") StatusQuarto excluirStatus,
                                   @Param("statusAtivos") Collection<StatusReserva> statusAtivos);

    boolean existsByNumero(String numero);

    boolean existsByNumeroAndIdNot(String numero, Long id);

    boolean existsByTipoQuartoId(Long tipoQuartoId);
}
