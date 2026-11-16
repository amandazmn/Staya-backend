package com.staya.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProdutoControllerTest {

    @Autowired
    MockMvc mvc;

    private ResultActions criar(String json) throws Exception {
        return mvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void criaComAtivoPorPadraoEFiltraPorCategoriaESituacao() throws Exception {
        criar("{\"nome\":\"Refrigerante\",\"categoria\":\"FRIGOBAR\",\"preco\":7.00}")
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.ativo").value(true));
        criar("{\"nome\":\"Agua com gas\",\"categoria\":\"FRIGOBAR\",\"preco\":5.00}").andExpect(status().isCreated());
        criar("{\"nome\":\"Prato executivo\",\"categoria\":\"RESTAURANTE\",\"preco\":49.90,\"ativo\":false}")
                .andExpect(status().isCreated());

        mvc.perform(get("/produtos")).andExpect(jsonPath("$", hasSize(3)));
        mvc.perform(get("/produtos").param("categoria", "FRIGOBAR"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome").value("Agua com gas"));
        mvc.perform(get("/produtos").param("ativo", "true")).andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(get("/produtos").param("ativo", "false"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Prato executivo"));
    }

    @Test
    void validaCamposENomeUnico() throws Exception {
        criar("{\"nome\":\"\",\"preco\":-1}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nome").exists())
                .andExpect(jsonPath("$.erros.categoria").exists())
                .andExpect(jsonPath("$.erros.preco").exists());

        criar("{\"nome\":\"Café\",\"categoria\":\"RESTAURANTE\",\"preco\":6.00}").andExpect(status().isCreated());
        criar("{\"nome\":\"CAFÉ\",\"categoria\":\"RESTAURANTE\",\"preco\":6.00}").andExpect(status().isConflict());
    }

    @Test
    void excluiProdutoNuncaLancado() throws Exception {
        String body = criar("{\"nome\":\"Toalha extra\",\"categoria\":\"SERVICO\",\"preco\":0}")
                .andReturn().getResponse().getContentAsString();
        long id = ((Number) com.jayway.jsonpath.JsonPath.read(body, "$.id")).longValue();

        mvc.perform(delete("/produtos/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/produtos/" + id)).andExpect(status().isNotFound());
    }
}
