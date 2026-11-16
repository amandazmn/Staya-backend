package com.staya.api.repository;

import com.staya.api.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByEstadiaIdOrderByPagoEmAscIdAsc(Long estadiaId);

    Optional<Pagamento> findByIdAndEstadiaId(Long id, Long estadiaId);
}
