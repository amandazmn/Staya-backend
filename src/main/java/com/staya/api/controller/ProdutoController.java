package com.staya.api.controller;

import com.staya.api.dto.ProdutoRequestDTO;
import com.staya.api.dto.ProdutoResponseDTO;
import com.staya.api.model.CategoriaProduto;
import com.staya.api.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    /** Ex.: GET /produtos?categoria=FRIGOBAR&ativo=true (filtros opcionais). */
    @GetMapping
    public List<ProdutoResponseDTO> listar(@RequestParam(required = false) CategoriaProduto categoria,
                                           @RequestParam(required = false) Boolean ativo) {
        return service.listar(categoria, ativo);
    }

    @GetMapping("/{id}")
    public ProdutoResponseDTO buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(@RequestBody @Valid ProdutoRequestDTO dto) {
        ProdutoResponseDTO criado = service.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ProdutoResponseDTO atualizar(@PathVariable Long id, @RequestBody @Valid ProdutoRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
