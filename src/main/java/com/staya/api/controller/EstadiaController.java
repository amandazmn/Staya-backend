package com.staya.api.controller;

import com.staya.api.dto.CheckinDiretoRequestDTO;
import com.staya.api.dto.CheckinRequestDTO;
import com.staya.api.dto.EstadiaResponseDTO;
import com.staya.api.model.StatusEstadia;
import com.staya.api.service.EstadiaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/estadias")
public class EstadiaController {

    private final EstadiaService service;

    public EstadiaController(EstadiaService service) {
        this.service = service;
    }

    /** Ex.: GET /estadias?status=EM_ANDAMENTO (hóspedes no hotel agora). Filtros opcionais. */
    @GetMapping
    public List<EstadiaResponseDTO> listar(@RequestParam(required = false) StatusEstadia status,
                                           @RequestParam(required = false) Long quartoId,
                                           @RequestParam(required = false) Long clienteId) {
        return service.listar(status, quartoId, clienteId);
    }

    @GetMapping("/{id}")
    public EstadiaResponseDTO buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping("/checkin")
    public ResponseEntity<EstadiaResponseDTO> checkin(@RequestBody @Valid CheckinRequestDTO dto) {
        return criado(service.checkin(dto));
    }

    @PostMapping("/checkin-direto")
    public ResponseEntity<EstadiaResponseDTO> checkinDireto(@RequestBody @Valid CheckinDiretoRequestDTO dto) {
        return criado(service.checkinDireto(dto));
    }

    @PostMapping("/{id}/checkout")
    public EstadiaResponseDTO checkout(@PathVariable Long id) {
        return service.checkout(id);
    }

    private ResponseEntity<EstadiaResponseDTO> criado(EstadiaResponseDTO estadia) {
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/estadias/{id}").buildAndExpand(estadia.getId()).toUri();
        return ResponseEntity.created(location).body(estadia);
    }
}
