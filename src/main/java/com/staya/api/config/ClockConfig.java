package com.staya.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/**
 * Relógio da aplicação no fuso do hotel. Toda regra que depende de "hoje" usa este bean,
 * para não depender do fuso do servidor (em UTC, depois das 21h de Brasília já seria "amanhã")
 * e para que os testes possam simular a passagem do tempo.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock(@Value("${staya.fuso-horario:America/Sao_Paulo}") String fusoHorario) {
        return Clock.system(ZoneId.of(fusoHorario));
    }
}
