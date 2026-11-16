package com.staya.api.repository;

import com.staya.api.model.Consumo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsumoRepository extends JpaRepository<Consumo, Long> {

    List<Consumo> findByEstadiaIdOrderByLancadoEmAscIdAsc(Long estadiaId);

    Optional<Consumo> findByIdAndEstadiaId(Long id, Long estadiaId);

    boolean existsByProdutoId(Long produtoId);
}
