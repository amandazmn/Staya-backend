package com.staya.api.controller;

import com.staya.api.dto.TipoQuartoRequestDTO;
import com.staya.api.dto.TipoQuartoResponseDTO;
import com.staya.api.service.TipoQuartoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tipos-quarto")
public class TipoQuartoController {

    private final TipoQuartoService service;

    public TipoQuartoController(TipoQuartoService service) {
        this.service = service;
    }

    @GetMapping
    public List<TipoQuartoResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public TipoQuartoResponseDTO buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<TipoQuartoResponseDTO> criar(@RequestBody @Valid TipoQuartoRequestDTO dto) {
        TipoQuartoResponseDTO criado = service.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public TipoQuartoResponseDTO atualizar(@PathVariable Long id, @RequestBody @Valid TipoQuartoRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
