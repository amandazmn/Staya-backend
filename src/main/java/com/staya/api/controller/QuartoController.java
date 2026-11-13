package com.staya.api.controller;

import com.staya.api.dto.QuartoRequestDTO;
import com.staya.api.dto.QuartoResponseDTO;
import com.staya.api.dto.QuartoStatusDTO;
import com.staya.api.model.StatusQuarto;
import com.staya.api.service.QuartoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/quartos")
public class QuartoController {

    private final QuartoService service;

    public QuartoController(QuartoService service) {
        this.service = service;
    }

    /** Ex.: GET /quartos?status=DISPONIVEL&tipoQuartoId=2&andar=3 (todos os filtros são opcionais). */
    @GetMapping
    public List<QuartoResponseDTO> listar(@RequestParam(required = false) StatusQuarto status,
                                          @RequestParam(required = false) Long tipoQuartoId,
                                          @RequestParam(required = false) Integer andar) {
        return service.listar(status, tipoQuartoId, andar);
    }

    @GetMapping("/{id}")
    public QuartoResponseDTO buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<QuartoResponseDTO> criar(@RequestBody @Valid QuartoRequestDTO dto) {
        QuartoResponseDTO criado = service.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public QuartoResponseDTO atualizar(@PathVariable Long id, @RequestBody @Valid QuartoRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @PatchMapping("/{id}/status")
    public QuartoResponseDTO alterarStatus(@PathVariable Long id, @RequestBody @Valid QuartoStatusDTO dto) {
        return service.alterarStatus(id, dto.getStatus());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
