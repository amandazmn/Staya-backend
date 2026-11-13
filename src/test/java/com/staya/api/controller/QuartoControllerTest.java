package com.staya.api.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class QuartoControllerTest {

    @Autowired
    MockMvc mvc;

    private long criar(String url, String json) throws Exception {
        String body = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private long criarTipo(String nome) throws Exception {
        return criar("/tipos-quarto", """
                {"nome":"%s","descricao":"Cama de casal","capacidade":2,"tarifaBase":350.00}""".formatted(nome));
    }

    private String quarto(String numero, Integer andar, long tipoId) {
        return """
                {"numero":"%s","andar":%s,"tipoQuartoId":%d}""".formatted(numero, andar, tipoId);
    }

    @Test
    void criaTipoEQuartoComStatusInicialDisponivel() throws Exception {
        long tipoId = criarTipo("Standard Casal");

        mvc.perform(post("/quartos").contentType(MediaType.APPLICATION_JSON).content(quarto("101", 1, tipoId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DISPONIVEL"))
                .andExpect(jsonPath("$.tipoQuarto.nome").value("Standard Casal"))
                .andExpect(jsonPath("$.tipoQuarto.tarifaBase").value(350.00));
    }

    @Test
    void validaTipoDeQuarto() throws Exception {
        mvc.perform(post("/tipos-quarto").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"capacidade\":0,\"tarifaBase\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nome").exists())
                .andExpect(jsonPath("$.erros.capacidade").exists())
                .andExpect(jsonPath("$.erros.tarifaBase").exists());
    }

    @Test
    void rejeitaNomeDeTipoDuplicadoIgnorandoMaiusculas() throws Exception {
        criarTipo("Luxo");
        mvc.perform(post("/tipos-quarto").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"LUXO\",\"capacidade\":2,\"tarifaBase\":500}"))
                .andExpect(status().isConflict());
    }

    @Test
    void rejeitaNumeroDeQuartoDuplicado() throws Exception {
        long tipoId = criarTipo("Standard");
        criar("/quartos", quarto("101", 1, tipoId));
        mvc.perform(post("/quartos").contentType(MediaType.APPLICATION_JSON).content(quarto("101", 1, tipoId)))
                .andExpect(status().isConflict());
    }

    @Test
    void rejeitaQuartoComTipoInexistente() throws Exception {
        mvc.perform(post("/quartos").contentType(MediaType.APPLICATION_JSON).content(quarto("101", 1, 999)))
                .andExpect(status().isNotFound());
    }

    @Test
    void filtraQuartosPorStatusTipoEAndar() throws Exception {
        long standard = criarTipo("Standard");
        long luxo = criarTipo("Luxo");
        long q101 = criar("/quartos", quarto("101", 1, standard));
        criar("/quartos", quarto("102", 1, standard));
        criar("/quartos", quarto("201", 2, luxo));

        mvc.perform(patch("/quartos/" + q101 + "/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"MANUTENCAO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("MANUTENCAO"));

        mvc.perform(get("/quartos")).andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].numero").value("101"));
        mvc.perform(get("/quartos").param("status", "DISPONIVEL")).andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(get("/quartos").param("tipoQuartoId", String.valueOf(luxo)))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].numero").value("201"));
        mvc.perform(get("/quartos").param("andar", "1").param("status", "DISPONIVEL"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].numero").value("102"));
    }

    @Test
    void rejeitaStatusInvalido() throws Exception {
        long id = criar("/quartos", quarto("101", 1, criarTipo("Standard")));
        mvc.perform(patch("/quartos/" + id + "/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INEXISTENTE\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/quartos").param("status", "INEXISTENTE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoExcluiTipoComQuartosNemQuartoOcupado() throws Exception {
        long tipoId = criarTipo("Standard");
        long quartoId = criar("/quartos", quarto("101", 1, tipoId));

        mvc.perform(delete("/tipos-quarto/" + tipoId)).andExpect(status().isConflict());

        mvc.perform(patch("/quartos/" + quartoId + "/status").contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"OCUPADO\"}")).andExpect(status().isOk());
        mvc.perform(delete("/quartos/" + quartoId)).andExpect(status().isConflict());

        mvc.perform(patch("/quartos/" + quartoId + "/status").contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"LIMPEZA\"}")).andExpect(status().isOk());
        mvc.perform(delete("/quartos/" + quartoId)).andExpect(status().isNoContent());
        mvc.perform(delete("/tipos-quarto/" + tipoId)).andExpect(status().isNoContent());
    }

    @Test
    void atualizaQuartoSemAlterarStatus() throws Exception {
        long tipoId = criarTipo("Standard");
        long luxo = criarTipo("Luxo");
        long id = criar("/quartos", quarto("101", 1, tipoId));
        mvc.perform(patch("/quartos/" + id + "/status").contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"LIMPEZA\"}"));

        mvc.perform(put("/quartos/" + id).contentType(MediaType.APPLICATION_JSON).content(quarto("101A", 1, luxo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value("101A"))
                .andExpect(jsonPath("$.tipoQuarto.nome").value("Luxo"))
                .andExpect(jsonPath("$.status").value("LIMPEZA"));
    }
}
