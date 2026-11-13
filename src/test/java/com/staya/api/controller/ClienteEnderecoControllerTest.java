package com.staya.api.controller;

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
class ClienteEnderecoControllerTest {

    private static final String ENDERECO = """
            {"logradouro":"Rua das Flores","numero":"100","cidade":"Florianópolis",
             "estado":"SC","pais":"Brasil","cep":"88000000"}""";

    @Autowired
    MockMvc mvc;

    private long criarEndereco() throws Exception {
        String body = mvc.perform(post("/enderecos").contentType(MediaType.APPLICATION_JSON).content(ENDERECO))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();
        return Long.parseLong(body.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    private String cliente(String cpf, String passaporte, Long enderecoId) {
        return """
                {"nome":"Ana","sobrenome":"Silva","cpf":%s,"passaporte":%s,
                 "email":"ana@example.com","nacionalidade":"Brasileira","enderecoId":%s}"""
                .formatted(json(cpf), json(passaporte), enderecoId);
    }

    private static String json(String v) {
        return v == null ? "null" : "\"" + v + "\"";
    }

    @Test
    void criaClienteComEnderecoEListaComEnderecoAninhado() throws Exception {
        long enderecoId = criarEndereco();

        mvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON)
                        .content(cliente("12345678901", null, enderecoId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.endereco.cidade").value("Florianópolis"));

        mvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cpf").value("12345678901"));
    }

    @Test
    void rejeitaClienteSemDocumento() throws Exception {
        mvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON)
                        .content(cliente(null, "  ", null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.documentoInformado").value("Informe o CPF ou o passaporte"));
    }

    @Test
    void rejeitaCpfInvalido() throws Exception {
        mvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON)
                        .content(cliente("123.456.789-01", null, null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.cpf").exists());
    }

    @Test
    void rejeitaCpfDuplicado() throws Exception {
        mvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON)
                        .content(cliente("12345678901", null, null)))
                .andExpect(status().isCreated());

        mvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON)
                        .content(cliente("12345678901", null, null)))
                .andExpect(status().isConflict());
    }

    @Test
    void estrangeiroComPassaporteSemCpf() throws Exception {
        mvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON)
                        .content(cliente("", "AB123456", null)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf").doesNotExist());
    }

    @Test
    void retorna404ParaEnderecoInexistente() throws Exception {
        mvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON)
                        .content(cliente("12345678901", null, 999L)))
                .andExpect(status().isNotFound());
        mvc.perform(get("/clientes/999")).andExpect(status().isNotFound());
    }

    @Test
    void rejeitaEnderecoSemCamposObrigatorios() throws Exception {
        mvc.perform(post("/enderecos").contentType(MediaType.APPLICATION_JSON).content("{\"numero\":\"1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.logradouro").exists())
                .andExpect(jsonPath("$.erros.cidade").exists())
                .andExpect(jsonPath("$.erros.pais").exists());
    }

    @Test
    void naoExcluiEnderecoEmUso() throws Exception {
        long enderecoId = criarEndereco();
        mvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON)
                        .content(cliente("12345678901", null, enderecoId)))
                .andExpect(status().isCreated());

        mvc.perform(delete("/enderecos/" + enderecoId)).andExpect(status().isConflict());
    }

    @Test
    void atualizaEExcluiCliente() throws Exception {
        String body = mvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON)
                        .content(cliente("12345678901", null, null)))
                .andReturn().getResponse().getContentAsString();
        long id = Long.parseLong(body.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mvc.perform(put("/clientes/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(cliente("12345678901", "XY999", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passaporte").value("XY999"));

        mvc.perform(delete("/clientes/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/clientes/" + id)).andExpect(status().isNotFound());
    }
}
