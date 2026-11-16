package com.staya.api.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/** Relógio controlável nos testes: começa em "agora" e pode avançar dias para simular a estadia. */
public class RelogioDeTeste extends Clock {

    private final ZoneId zona;
    private Instant instante;

    public RelogioDeTeste(ZoneId zona) {
        this.zona = zona;
        this.instante = Instant.now();
    }

    public void reiniciar() {
        instante = Instant.now();
    }

    public void avancarDias(long dias) {
        instante = instante.plus(Duration.ofDays(dias));
    }

    @Override
    public ZoneId getZone() {
        return zona;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        RelogioDeTeste outro = new RelogioDeTeste(zone);
        outro.instante = instante;
        return outro;
    }

    @Override
    public Instant instant() {
        return instante;
    }

    @TestConfiguration
    public static class Config {
        @Bean
        @Primary
        public RelogioDeTeste relogioDeTeste() {
            // Mesmo fuso que os testes usam em LocalDate.now()
            return new RelogioDeTeste(ZoneId.systemDefault());
        }
    }
}
