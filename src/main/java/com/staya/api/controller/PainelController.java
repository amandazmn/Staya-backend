package com.staya.api.controller;

import com.staya.api.dto.PainelDiaDTO;
import com.staya.api.service.PainelService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/painel")
public class PainelController {

    private final PainelService service;

    public PainelController(PainelService service) {
        this.service = service;
    }

    /** Ex.: GET /painel/dia (hoje) ou GET /painel/dia?data=2026-12-24 */
    @GetMapping("/dia")
    public PainelDiaDTO dia(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return service.painel(data);
    }
}
