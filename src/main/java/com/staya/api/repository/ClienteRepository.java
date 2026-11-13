package com.staya.api.repository;

import com.staya.api.model.Cliente;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Override
    @EntityGraph(attributePaths = "endereco")
    List<Cliente> findAll();

    @Override
    @EntityGraph(attributePaths = "endereco")
    Optional<Cliente> findById(Long id);

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    boolean existsByPassaporte(String passaporte);

    boolean existsByPassaporteAndIdNot(String passaporte, Long id);

    boolean existsByEnderecoId(Long enderecoId);
}
