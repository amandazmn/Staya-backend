package com.staya.api.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReservaControllerTest {

    /** Datas relativas a hoje para o teste não "vencer" com o tempo. */
    private static final LocalDate D10 = LocalDate.now().plusDays(10);

    @Autowired
    MockMvc mvc;

    private long clienteId;
    private long quartoCasal;   // capacidade 2, R$ 300,00
    private long quartoFamilia; // capacidade 4, R$ 500,00

    private long criar(String url, String json) throws Exception {
        String body = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    @BeforeEach
    void dados() throws Exception {
        clienteId = criar("/clientes", """
                {"nome":"Ana","sobrenome":"Silva","cpf":"12345678901","nacionalidade":"Brasileira"}""");
        long casal = criar("/tipos-quarto", """
                {"nome":"Casal","capacidade":2,"tarifaBase":300.00}""");
        long familia = criar("/tipos-quarto", """
                {"nome":"Família","capacidade":4,"tarifaBase":500.00}""");
        quartoCasal = criar("/quartos", "{\"numero\":\"101\",\"andar\":1,\"tipoQuartoId\":" + casal + "}");
        quartoFamilia = criar("/quartos", "{\"numero\":\"201\",\"andar\":2,\"tipoQuartoId\":" + familia + "}");
    }

    private String reserva(long quartoId, LocalDate entrada, LocalDate saida, int hospedes) {
        return """
                {"clienteId":%d,"quartoId":%d,"dataEntrada":"%s","dataSaida":"%s","quantidadeHospedes":%d}"""
                .formatted(clienteId, quartoId, entrada, saida, hospedes);
    }

    private ResultActions postReserva(long quartoId, LocalDate entrada, LocalDate saida, int hospedes) throws Exception {
        return mvc.perform(post("/reservas").contentType(MediaType.APPLICATION_JSON)
                .content(reserva(quartoId, entrada, saida, hospedes)));
    }

    @Test
    void criaReservaPendenteComValorCalculado() throws Exception {
        postReserva(quartoCasal, D10, D10.plusDays(3), 2)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.quantidadeDiarias").value(3))
                .andExpect(jsonPath("$.valorDiaria").value(300.00))
                .andExpect(jsonPath("$.valorTotal").value(900.00))
                .andExpect(jsonPath("$.clienteNome").value("Ana Silva"))
                .andExpect(jsonPath("$.quartoNumero").value("101"))
                .andExpect(jsonPath("$.dataEntrada").value(D10.toString()))
                .andExpect(jsonPath("$.criadoEm").exists());
    }

    @Test
    void impedeReservasSobrepostasNoMesmoQuarto() throws Exception {
        postReserva(quartoCasal, D10, D10.plusDays(3), 1).andExpect(status().isCreated());

        postReserva(quartoCasal, D10.plusDays(2), D10.plusDays(5), 1).andExpect(status().isConflict());
        postReserva(quartoCasal, D10.minusDays(1), D10.plusDays(1), 1).andExpect(status().isConflict());
        postReserva(quartoCasal, D10.plusDays(1), D10.plusDays(2), 1).andExpect(status().isConflict());
        // Outro quarto no mesmo período: tudo bem
        postReserva(quartoFamilia, D10, D10.plusDays(3), 1).andExpect(status().isCreated());
    }

    @Test
    void permiteEntradaNoDiaDaSaidaAnterior() throws Exception {
        postReserva(quartoCasal, D10, D10.plusDays(3), 1).andExpect(status().isCreated());
        postReserva(quartoCasal, D10.plusDays(3), D10.plusDays(5), 1).andExpect(status().isCreated());
        postReserva(quartoCasal, D10.minusDays(2), D10, 1).andExpect(status().isCreated());
    }

    @Test
    void cancelamentoLiberaOPeriodo() throws Exception {
        long id = criar("/reservas", reserva(quartoCasal, D10, D10.plusDays(3), 1));
        mvc.perform(patch("/reservas/" + id + "/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));

        postReserva(quartoCasal, D10, D10.plusDays(3), 1).andExpect(status().isCreated());
    }

    @Test
    void validaPeriodoEDatas() throws Exception {
        postReserva(quartoCasal, D10, D10, 1)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.periodoValido").exists());
        postReserva(quartoCasal, LocalDate.now().minusDays(1), D10, 1)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.dataEntrada").exists());
        mvc.perform(post("/reservas").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.clienteId").exists())
                .andExpect(jsonPath("$.erros.quartoId").exists());
    }

    @Test
    void respeitaCapacidadeDoQuarto() throws Exception {
        postReserva(quartoCasal, D10, D10.plusDays(1), 3)
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value("Quarto 101 comporta no máximo 2 hóspede(s)"));
        postReserva(quartoFamilia, D10, D10.plusDays(1), 3).andExpect(status().isCreated());
    }

    @Test
    void naoReservaQuartoBloqueado() throws Exception {
        mvc.perform(patch("/quartos/" + quartoCasal + "/status").contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"BLOQUEADO\"}")).andExpect(status().isOk());
        postReserva(quartoCasal, D10, D10.plusDays(1), 1).andExpect(status().isUnprocessableContent());
    }

    @Test
    void retorna404ParaClienteOuQuartoInexistente() throws Exception {
        postReserva(999, D10, D10.plusDays(1), 1).andExpect(status().isNotFound());
        mvc.perform(post("/reservas").contentType(MediaType.APPLICATION_JSON).content("""
                        {"clienteId":999,"quartoId":%d,"dataEntrada":"%s","dataSaida":"%s","quantidadeHospedes":1}"""
                        .formatted(quartoCasal, D10, D10.plusDays(1))))
                .andExpect(status().isNotFound());
        mvc.perform(get("/reservas/999")).andExpect(status().isNotFound());
    }

    @Test
    void fluxoDeStatus() throws Exception {
        long id = criar("/reservas", reserva(quartoCasal, D10, D10.plusDays(2), 1));

        mvc.perform(patch("/reservas/" + id + "/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMADA"));
        mvc.perform(patch("/reservas/" + id + "/confirmar")).andExpect(status().isConflict());

        mvc.perform(patch("/reservas/" + id + "/cancelar")).andExpect(status().isOk());
        mvc.perform(patch("/reservas/" + id + "/cancelar")).andExpect(status().isConflict());
        mvc.perform(put("/reservas/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(reserva(quartoCasal, D10, D10.plusDays(3), 1)))
                .andExpect(status().isConflict());
    }

    @Test
    void atualizaReservaRecalculandoValorSemConflitarConsigoMesma() throws Exception {
        long id = criar("/reservas", reserva(quartoCasal, D10, D10.plusDays(2), 1));

        // Estender o próprio período não pode conflitar com ela mesma
        mvc.perform(put("/reservas/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(reserva(quartoCasal, D10, D10.plusDays(4), 2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeDiarias").value(4))
                .andExpect(jsonPath("$.valorTotal").value(1200.00));

        // Trocar para o quarto família recalcula pela tarifa dele
        mvc.perform(put("/reservas/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(reserva(quartoFamilia, D10, D10.plusDays(4), 2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quartoNumero").value("201"))
                .andExpect(jsonPath("$.valorTotal").value(2000.00));
    }

    @Test
    void tarifaFicaCongeladaNaReserva() throws Exception {
        long id = criar("/reservas", reserva(quartoCasal, D10, D10.plusDays(2), 1));
        String quarto = mvc.perform(get("/quartos/" + quartoCasal)).andReturn().getResponse().getContentAsString();
        long tipoId = ((Number) JsonPath.read(quarto, "$.tipoQuarto.id")).longValue();

        mvc.perform(put("/tipos-quarto/" + tipoId).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Casal\",\"capacidade\":2,\"tarifaBase\":450.00}")).andExpect(status().isOk());

        mvc.perform(get("/reservas/" + id))
                .andExpect(jsonPath("$.valorDiaria").value(300.00))
                .andExpect(jsonPath("$.valorTotal").value(600.00));
    }

    @Test
    void filtraReservas() throws Exception {
        long id = criar("/reservas", reserva(quartoCasal, D10, D10.plusDays(2), 1));
        criar("/reservas", reserva(quartoFamilia, D10, D10.plusDays(2), 1));
        mvc.perform(patch("/reservas/" + id + "/confirmar"));

        mvc.perform(get("/reservas")).andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(get("/reservas").param("status", "CONFIRMADA"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].quartoNumero").value("101"));
        mvc.perform(get("/reservas").param("quartoId", String.valueOf(quartoFamilia)))
                .andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/reservas").param("clienteId", String.valueOf(clienteId)))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void listaQuartosDisponiveis() throws Exception {
        criar("/reservas", reserva(quartoCasal, D10, D10.plusDays(3), 1));

        mvc.perform(get("/quartos/disponiveis")
                        .param("entrada", D10.plusDays(1).toString()).param("saida", D10.plusDays(2).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].numero").value("201"));

        // Depois da saída o 101 volta a aparecer
        mvc.perform(get("/quartos/disponiveis")
                        .param("entrada", D10.plusDays(3).toString()).param("saida", D10.plusDays(4).toString()))
                .andExpect(jsonPath("$", hasSize(2)));

        // Filtro por capacidade
        mvc.perform(get("/quartos/disponiveis").param("hospedes", "3")
                        .param("entrada", D10.plusDays(3).toString()).param("saida", D10.plusDays(4).toString()))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].numero").value("201"));
    }

    @Test
    void validaParametrosDeDisponibilidade() throws Exception {
        mvc.perform(get("/quartos/disponiveis").param("entrada", D10.toString()))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/quartos/disponiveis").param("entrada", "20-12-2026").param("saida", D10.toString()))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/quartos/disponiveis").param("entrada", D10.toString()).param("saida", D10.toString()))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void naoExcluiClienteNemQuartoComReservas() throws Exception {
        criar("/reservas", reserva(quartoCasal, D10, D10.plusDays(1), 1));
        mvc.perform(delete("/clientes/" + clienteId)).andExpect(status().isConflict());
        mvc.perform(delete("/quartos/" + quartoCasal)).andExpect(status().isConflict());
    }
}
