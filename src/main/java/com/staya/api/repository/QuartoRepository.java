package com.staya.api.repository;

import com.staya.api.model.Quarto;
import com.staya.api.model.StatusQuarto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    boolean existsByNumero(String numero);

    boolean existsByNumeroAndIdNot(String numero, Long id);

    boolean existsByTipoQuartoId(Long tipoQuartoId);
}
