package com.staya.api.controller;

import com.jayway.jsonpath.JsonPath;
import com.staya.api.support.RelogioDeTeste;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(RelogioDeTeste.Config.class)
class PainelControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    RelogioDeTeste relogio;

    private LocalDate hoje;
    private long ana;
    private long john;
    private long q101;
    private long q102;
    private long q103;

    private long criar(String url, String json) throws Exception {
        String body = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    /** 4 quartos de capacidade 2; o 104 fica BLOQUEADO (fora da venda). */
    @BeforeEach
    void dados() throws Exception {
        relogio.reiniciar();
        hoje = LocalDate.now(relogio);
        ana = criar("/clientes", """
                {"nome":"Ana","sobrenome":"Silva","cpf":"12345678901","nacionalidade":"Brasileira"}""");
        john = criar("/clientes", """
                {"nome":"John","sobrenome":"Smith","passaporte":"US123","nacionalidade":"Americana"}""");
        long tipo = criar("/tipos-quarto", "{\"nome\":\"Casal\",\"capacidade\":2,\"tarifaBase\":300.00}");
        q101 = criar("/quartos", "{\"numero\":\"101\",\"andar\":1,\"tipoQuartoId\":" + tipo + "}");
        q102 = criar("/quartos", "{\"numero\":\"102\",\"andar\":1,\"tipoQuartoId\":" + tipo + "}");
        q103 = criar("/quartos", "{\"numero\":\"103\",\"andar\":1,\"tipoQuartoId\":" + tipo + "}");
        long q104 = criar("/quartos", "{\"numero\":\"104\",\"andar\":1,\"tipoQuartoId\":" + tipo + "}");
        mvc.perform(patch("/quartos/" + q104 + "/status").contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"BLOQUEADO\"}")).andExpect(status().isOk());
    }

    private long reserva(long quarto, LocalDate entrada, LocalDate saida, boolean confirmar) throws Exception {
        long id = criar("/reservas", """
                {"clienteId":%d,"quartoId":%d,"dataEntrada":"%s","dataSaida":"%s","quantidadeHospedes":1}"""
                .formatted(ana, quarto, entrada, saida));
        if (confirmar) {
            mvc.perform(patch("/reservas/" + id + "/confirmar")).andExpect(status().isOk());
        }
        return id;
    }

    private long checkin(long reservaId, Long... acompanhantes) throws Exception {
        String ids = String.join(",", java.util.Arrays.stream(acompanhantes).map(String::valueOf).toList());
        return criar("/estadias/checkin", "{\"reservaId\":%d,\"acompanhanteIds\":[%s]}".formatted(reservaId, ids));
    }

    @Test
    void painelSemMovimento() throws Exception {
        mvc.perform(get("/painel/dia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(hoje.toString()))
                .andExpect(jsonPath("$.chegadasPrevistas", hasSize(0)))
                .andExpect(jsonPath("$.saidasPrevistas", hasSize(0)))
                .andExpect(jsonPath("$.quartosPorStatus.DISPONIVEL").value(3))
                .andExpect(jsonPath("$.quartosPorStatus.BLOQUEADO").value(1))
                .andExpect(jsonPath("$.quartosPorStatus.OCUPADO").value(0))
                .andExpect(jsonPath("$.ocupacao.quartosOcupados").value(0))
                .andExpect(jsonPath("$.ocupacao.quartosVendaveis").value(3))
                .andExpect(jsonPath("$.ocupacao.taxaPercentual").value(0.0))
                .andExpect(jsonPath("$.hospedesNoHotel").value(0));
    }

    @Test
    void chegadasPrevistasERealizadas() throws Exception {
        long r101 = reserva(q101, hoje, hoje.plusDays(2), true);
        reserva(q102, hoje, hoje.plusDays(2), false);           // pendente também aparece
        reserva(q103, hoje.plusDays(1), hoje.plusDays(2), true); // amanhã: não aparece hoje

        mvc.perform(get("/painel/dia"))
                .andExpect(jsonPath("$.chegadasPrevistas", hasSize(2)))
                .andExpect(jsonPath("$.chegadasPrevistas[0].quartoNumero").value("101"))
                .andExpect(jsonPath("$.chegadasPrevistas[1].status").value("PENDENTE"));

        checkin(r101, john);

        mvc.perform(get("/painel/dia"))
                .andExpect(jsonPath("$.chegadasPrevistas", hasSize(1)))
                .andExpect(jsonPath("$.chegadasPrevistas[0].quartoNumero").value("102"))
                .andExpect(jsonPath("$.chegadasRealizadas", hasSize(1)))
                .andExpect(jsonPath("$.chegadasRealizadas[0].quartoNumero").value("101"))
                .andExpect(jsonPath("$.quartosPorStatus.OCUPADO").value(1))
                .andExpect(jsonPath("$.hospedesNoHotel").value(2))
                // 101 (hospedado) e 102 (pendente) ocupam a noite; 3 vendáveis
                .andExpect(jsonPath("$.ocupacao.quartosOcupados").value(2))
                .andExpect(jsonPath("$.ocupacao.taxaPercentual").value(66.7));
    }

    @Test
    void reservaQueNaoChegouApareceComoAtrasada() throws Exception {
        reserva(q101, hoje, hoje.plusDays(3), true);
        relogio.avancarDias(1);

        mvc.perform(get("/painel/dia"))
                .andExpect(jsonPath("$.chegadasPrevistas", hasSize(0)))
                .andExpect(jsonPath("$.chegadasAtrasadas", hasSize(1)))
                .andExpect(jsonPath("$.chegadasAtrasadas[0].dataEntrada").value(hoje.toString()));
    }

    @Test
    void saidasPrevistasAtrasadasERealizadas() throws Exception {
        long estadia = checkin(reserva(q101, hoje, hoje.plusDays(2), true));

        relogio.avancarDias(2);
        mvc.perform(get("/painel/dia"))
                .andExpect(jsonPath("$.saidasPrevistas", hasSize(1)))
                .andExpect(jsonPath("$.saidasPrevistas[0].id").value(estadia))
                .andExpect(jsonPath("$.saidasAtrasadas", hasSize(0)))
                // Hóspede ainda não fez check-out: o quarto segue contando como ocupado
                .andExpect(jsonPath("$.ocupacao.quartosOcupados").value(1));

        relogio.avancarDias(1);
        mvc.perform(get("/painel/dia"))
                .andExpect(jsonPath("$.saidasPrevistas", hasSize(0)))
                .andExpect(jsonPath("$.saidasAtrasadas", hasSize(1)))
                // Hóspede além do período continua ocupando o quarto
                .andExpect(jsonPath("$.ocupacao.quartosOcupados").value(1));

        mvc.perform(post("/estadias/" + estadia + "/checkout")).andExpect(status().isOk());
        mvc.perform(get("/painel/dia"))
                .andExpect(jsonPath("$.saidasAtrasadas", hasSize(0)))
                .andExpect(jsonPath("$.saidasRealizadas", hasSize(1)))
                .andExpect(jsonPath("$.quartosPorStatus.LIMPEZA").value(1))
                .andExpect(jsonPath("$.hospedesNoHotel").value(0));
    }

    @Test
    void consultaDeDataFuturaMostraPrevisao() throws Exception {
        LocalDate natal = hoje.plusDays(10);
        reserva(q101, natal, natal.plusDays(3), true);
        reserva(q102, natal.minusDays(1), natal.plusDays(1), true);
        long cancelada = reserva(q103, natal, natal.plusDays(1), true);
        mvc.perform(patch("/reservas/" + cancelada + "/cancelar")).andExpect(status().isOk());

        mvc.perform(get("/painel/dia").param("data", natal.toString()))
                .andExpect(jsonPath("$.data").value(natal.toString()))
                .andExpect(jsonPath("$.chegadasPrevistas", hasSize(1)))
                .andExpect(jsonPath("$.chegadasPrevistas[0].quartoNumero").value("101"))
                .andExpect(jsonPath("$.ocupacao.quartosOcupados").value(2))
                .andExpect(jsonPath("$.ocupacao.taxaPercentual").value(66.7));
    }

    @Test
    void rejeitaDataInvalida() throws Exception {
        mvc.perform(get("/painel/dia").param("data", "24/12/2026")).andExpect(status().isBadRequest());
    }
}
