package com.staya.api.controller;

import com.staya.api.dto.*;
import com.staya.api.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estadias/{estadiaId}")
public class ContaController {

    private final ContaService service;

    public ContaController(ContaService service) {
        this.service = service;
    }

    @GetMapping("/conta")
    public ContaDTO conta(@PathVariable Long estadiaId) {
        return service.conta(estadiaId);
    }

    @PostMapping("/consumos")
    @ResponseStatus(HttpStatus.CREATED)
    public ConsumoResponseDTO lancarConsumo(@PathVariable Long estadiaId, @RequestBody @Valid ConsumoRequestDTO dto) {
        return service.lancarConsumo(estadiaId, dto);
    }

    @PostMapping("/consumos/{consumoId}/estorno")
    public ConsumoResponseDTO estornarConsumo(@PathVariable Long estadiaId, @PathVariable Long consumoId,
                                              @RequestBody @Valid EstornoRequestDTO dto) {
        return service.estornarConsumo(estadiaId, consumoId, dto);
    }

    @PostMapping("/pagamentos")
    @ResponseStatus(HttpStatus.CREATED)
    public PagamentoResponseDTO registrarPagamento(@PathVariable Long estadiaId,
                                                   @RequestBody @Valid PagamentoRequestDTO dto) {
        return service.registrarPagamento(estadiaId, dto);
    }

    @PostMapping("/pagamentos/{pagamentoId}/estorno")
    public PagamentoResponseDTO estornarPagamento(@PathVariable Long estadiaId, @PathVariable Long pagamentoId,
                                                  @RequestBody @Valid EstornoRequestDTO dto) {
        return service.estornarPagamento(estadiaId, pagamentoId, dto);
    }
}
