package com.staya.api.controller;

import com.staya.api.dto.ReservaRequestDTO;
import com.staya.api.dto.ReservaResponseDTO;
import com.staya.api.model.StatusReserva;
import com.staya.api.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Reservas não são excluídas: para manter o histórico, use o cancelamento.
 */
@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }

    /** Ex.: GET /reservas?clienteId=1&status=CONFIRMADA (todos os filtros são opcionais). */
    @GetMapping
    public List<ReservaResponseDTO> listar(@RequestParam(required = false) Long clienteId,
                                           @RequestParam(required = false) Long quartoId,
                                           @RequestParam(required = false) StatusReserva status) {
        return service.listar(clienteId, quartoId, status);
    }

    @GetMapping("/{id}")
    public ReservaResponseDTO buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> criar(@RequestBody @Valid ReservaRequestDTO dto) {
        ReservaResponseDTO criada = service.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @PutMapping("/{id}")
    public ReservaResponseDTO atualizar(@PathVariable Long id, @RequestBody @Valid ReservaRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @PatchMapping("/{id}/confirmar")
    public ReservaResponseDTO confirmar(@PathVariable Long id) {
        return service.confirmar(id);
    }

    @PatchMapping("/{id}/cancelar")
    public ReservaResponseDTO cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }
}
