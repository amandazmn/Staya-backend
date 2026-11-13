package com.staya.api.repository;

import com.staya.api.model.TipoQuarto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoQuartoRepository extends JpaRepository<TipoQuarto, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
