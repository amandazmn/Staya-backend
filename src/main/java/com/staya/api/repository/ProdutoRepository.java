package com.staya.api.repository;

import com.staya.api.model.CategoriaProduto;
import com.staya.api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    /** Filtros opcionais (null = sem filtro), por categoria e nome. */
    @Query("""
            select p from Produto p
            where (:categoria is null or p.categoria = :categoria)
              and (:ativo is null or p.ativo = :ativo)
            order by p.categoria, p.nome""")
    List<Produto> buscar(@Param("categoria") CategoriaProduto categoria, @Param("ativo") Boolean ativo);

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
