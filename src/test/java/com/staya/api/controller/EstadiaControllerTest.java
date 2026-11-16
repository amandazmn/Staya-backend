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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(RelogioDeTeste.Config.class)
class EstadiaControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    RelogioDeTeste relogio;

    private LocalDate hoje;
    private long titular;
    private long acompanhante;
    private long quartoCasal; // capacidade 2, R$ 300,00

    private long criar(String url, String json) throws Exception {
        String body = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    @BeforeEach
    void dados() throws Exception {
        relogio.reiniciar();
        hoje = LocalDate.now(relogio);
        titular = criar("/clientes", """
                {"nome":"Ana","sobrenome":"Silva","cpf":"12345678901","nacionalidade":"Brasileira"}""");
        acompanhante = criar("/clientes", """
                {"nome":"John","sobrenome":"Smith","passaporte":"US123","nacionalidade":"Americana"}""");
        long tipo = criar("/tipos-quarto", "{\"nome\":\"Casal\",\"capacidade\":2,\"tarifaBase\":300.00}");
        quartoCasal = criar("/quartos", "{\"numero\":\"101\",\"andar\":1,\"tipoQuartoId\":" + tipo + "}");
    }

    private long reservaConfirmada(LocalDate entrada, LocalDate saida) throws Exception {
        long id = criar("/reservas", """
                {"clienteId":%d,"quartoId":%d,"dataEntrada":"%s","dataSaida":"%s","quantidadeHospedes":1}"""
                .formatted(titular, quartoCasal, entrada, saida));
        mvc.perform(patch("/reservas/" + id + "/confirmar")).andExpect(status().isOk());
        return id;
    }

    private ResultActions checkin(long reservaId, Long... acompanhantes) throws Exception {
        String ids = String.join(",", java.util.Arrays.stream(acompanhantes).map(String::valueOf).toList());
        return mvc.perform(post("/estadias/checkin").contentType(MediaType.APPLICATION_JSON)
                .content("{\"reservaId\":%d,\"acompanhanteIds\":[%s]}".formatted(reservaId, ids)));
    }

    private long idDe(ResultActions resultado) throws Exception {
        return ((Number) JsonPath.read(resultado.andReturn().getResponse().getContentAsString(), "$.id")).longValue();
    }

    private void pagar(long estadiaId, String valor) throws Exception {
        mvc.perform(post("/estadias/" + estadiaId + "/pagamentos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"forma\":\"PIX\",\"valor\":" + valor + "}"))
                .andExpect(status().isCreated());
    }

    @Test
    void checkinOcupaQuartoEColocaReservaEmHospedagem() throws Exception {
        long reservaId = reservaConfirmada(hoje, hoje.plusDays(3));

        checkin(reservaId, acompanhante)
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"))
                .andExpect(jsonPath("$.titular.nome").value("Ana Silva"))
                .andExpect(jsonPath("$.acompanhantes[0].nome").value("John Smith"))
                .andExpect(jsonPath("$.quantidadeHospedes").value(2))
                .andExpect(jsonPath("$.checkinEm").exists())
                .andExpect(jsonPath("$.valorHospedagem").value(900.00));

        mvc.perform(get("/quartos/" + quartoCasal)).andExpect(jsonPath("$.status").value("OCUPADO"));
        mvc.perform(get("/reservas/" + reservaId))
                .andExpect(jsonPath("$.status").value("EM_HOSPEDAGEM"))
                .andExpect(jsonPath("$.quantidadeHospedes").value(2));
    }

    @Test
    void checkoutNoDiaPrevistoLiberaQuartoParaLimpeza() throws Exception {
        long estadiaId = idDe(checkin(reservaConfirmada(hoje, hoje.plusDays(3))));
        relogio.avancarDias(3);
        pagar(estadiaId, "900.00");

        mvc.perform(post("/estadias/" + estadiaId + "/checkout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENCERRADA"))
                .andExpect(jsonPath("$.checkoutEm").exists())
                .andExpect(jsonPath("$.quantidadeDiarias").value(3))
                .andExpect(jsonPath("$.valorHospedagem").value(900.00));

        mvc.perform(get("/quartos/" + quartoCasal)).andExpect(jsonPath("$.status").value("LIMPEZA"));
        mvc.perform(get("/estadias/" + estadiaId)).andExpect(jsonPath("$.status").value("ENCERRADA"));
        mvc.perform(post("/estadias/" + estadiaId + "/checkout")).andExpect(status().isConflict());
    }

    @Test
    void checkoutAntecipadoCobraDiariasUsadasELiberaOPeriodo() throws Exception {
        long reservaId = reservaConfirmada(hoje, hoje.plusDays(5));
        long estadiaId = idDe(checkin(reservaId));
        relogio.avancarDias(2);
        pagar(estadiaId, "600.00");

        mvc.perform(post("/estadias/" + estadiaId + "/checkout"))
                .andExpect(jsonPath("$.quantidadeDiarias").value(2))
                .andExpect(jsonPath("$.dataSaida").value(hoje.plusDays(2).toString()))
                .andExpect(jsonPath("$.valorHospedagem").value(600.00));
        mvc.perform(get("/reservas/" + reservaId)).andExpect(jsonPath("$.status").value("CONCLUIDA"));

        // Os dias não usados voltam a ficar livres para reserva
        mvc.perform(get("/quartos/disponiveis")
                        .param("entrada", hoje.plusDays(3).toString()).param("saida", hoje.plusDays(5).toString()))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void checkoutNoMesmoDiaCobraUmaDiaria() throws Exception {
        long estadiaId = idDe(checkin(reservaConfirmada(hoje, hoje.plusDays(2))));
        pagar(estadiaId, "300.00");
        mvc.perform(post("/estadias/" + estadiaId + "/checkout"))
                .andExpect(jsonPath("$.quantidadeDiarias").value(1))
                .andExpect(jsonPath("$.valorHospedagem").value(300.00));
    }

    @Test
    void exigeReservaConfirmada() throws Exception {
        long pendente = criar("/reservas", """
                {"clienteId":%d,"quartoId":%d,"dataEntrada":"%s","dataSaida":"%s","quantidadeHospedes":1}"""
                .formatted(titular, quartoCasal, hoje, hoje.plusDays(1)));
        checkin(pendente).andExpect(status().isConflict());
    }

    @Test
    void naoPermiteCheckinAntesDoDiaDeEntradaNemDuasVezes() throws Exception {
        long futura = reservaConfirmada(hoje.plusDays(2), hoje.plusDays(4));
        checkin(futura)
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").exists());

        long reservaHoje = reservaConfirmada(hoje, hoje.plusDays(1));
        checkin(reservaHoje).andExpect(status().isCreated());
        checkin(reservaHoje).andExpect(status().isConflict());
    }

    @Test
    void naoPermiteCheckinComPeriodoEncerrado() throws Exception {
        long reservaId = reservaConfirmada(hoje, hoje.plusDays(1));
        relogio.avancarDias(1);
        checkin(reservaId).andExpect(status().isUnprocessableContent());
    }

    @Test
    void exigeQuartoDisponivel() throws Exception {
        long reservaId = reservaConfirmada(hoje, hoje.plusDays(2));
        mvc.perform(patch("/quartos/" + quartoCasal + "/status").contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"LIMPEZA\"}")).andExpect(status().isOk());

        checkin(reservaId)
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value("Quarto 101 não está pronto para receber hóspedes (status: LIMPEZA)"));
    }

    @Test
    void validaAcompanhantes() throws Exception {
        long reservaId = reservaConfirmada(hoje, hoje.plusDays(2));
        long terceiro = criar("/clientes", """
                {"nome":"Bia","sobrenome":"Souza","cpf":"98765432100","nacionalidade":"Brasileira"}""");

        checkin(reservaId, titular).andExpect(status().isUnprocessableContent());
        checkin(reservaId, 999L).andExpect(status().isNotFound());
        checkin(reservaId, acompanhante, terceiro)
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value("Quarto 101 comporta no máximo 2 hóspede(s)"));
        // Ids repetidos contam uma vez só
        checkin(reservaId, acompanhante, acompanhante)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantidadeHospedes").value(2));
    }

    @Test
    void reservaEmHospedagemNaoPodeSerCanceladaNemAlterada() throws Exception {
        long reservaId = reservaConfirmada(hoje, hoje.plusDays(2));
        checkin(reservaId).andExpect(status().isCreated());

        mvc.perform(patch("/reservas/" + reservaId + "/cancelar")).andExpect(status().isConflict());
        mvc.perform(put("/reservas/" + reservaId).contentType(MediaType.APPLICATION_JSON).content("""
                        {"clienteId":%d,"quartoId":%d,"dataEntrada":"%s","dataSaida":"%s","quantidadeHospedes":1}"""
                        .formatted(titular, quartoCasal, hoje, hoje.plusDays(4))))
                .andExpect(status().isConflict());
    }

    @Test
    void quartoOcupadoSoSaiDoStatusPeloCheckout() throws Exception {
        checkin(reservaConfirmada(hoje, hoje.plusDays(2))).andExpect(status().isCreated());
        mvc.perform(patch("/quartos/" + quartoCasal + "/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"DISPONIVEL\"}"))
                .andExpect(status().isUnprocessableContent());
        mvc.perform(delete("/quartos/" + quartoCasal)).andExpect(status().isConflict());
    }

    @Test
    void checkinDiretoCriaReservaEHospeda() throws Exception {
        mvc.perform(post("/estadias/checkin-direto").contentType(MediaType.APPLICATION_JSON).content("""
                        {"clienteId":%d,"quartoId":%d,"dataSaida":"%s","acompanhanteIds":[%d]}"""
                        .formatted(titular, quartoCasal, hoje.plusDays(2), acompanhante)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dataEntrada").value(hoje.toString()))
                .andExpect(jsonPath("$.quantidadeHospedes").value(2))
                .andExpect(jsonPath("$.valorHospedagem").value(600.00));

        mvc.perform(get("/reservas").param("status", "EM_HOSPEDAGEM")).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/quartos/" + quartoCasal)).andExpect(jsonPath("$.status").value("OCUPADO"));
    }

    @Test
    void checkinDiretoRespeitaReservasExistentes() throws Exception {
        reservaConfirmada(hoje.plusDays(1), hoje.plusDays(3));

        mvc.perform(post("/estadias/checkin-direto").contentType(MediaType.APPLICATION_JSON).content("""
                        {"clienteId":%d,"quartoId":%d,"dataSaida":"%s"}"""
                        .formatted(acompanhante, quartoCasal, hoje.plusDays(2))))
                .andExpect(status().isConflict());

        // Sai antes da próxima reserva: tudo bem
        mvc.perform(post("/estadias/checkin-direto").contentType(MediaType.APPLICATION_JSON).content("""
                        {"clienteId":%d,"quartoId":%d,"dataSaida":"%s"}"""
                        .formatted(acompanhante, quartoCasal, hoje.plusDays(1))))
                .andExpect(status().isCreated());
    }

    @Test
    void checkinDiretoValidaDataDeSaida() throws Exception {
        mvc.perform(post("/estadias/checkin-direto").contentType(MediaType.APPLICATION_JSON).content("""
                        {"clienteId":%d,"quartoId":%d,"dataSaida":"%s"}""".formatted(titular, quartoCasal, hoje)))
                .andExpect(status().isUnprocessableContent());
        mvc.perform(post("/estadias/checkin-direto").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.dataSaida").exists());
    }

    @Test
    void listaEstadiasComFiltros() throws Exception {
        long estadiaId = idDe(checkin(reservaConfirmada(hoje, hoje.plusDays(2))));

        mvc.perform(get("/estadias").param("status", "EM_ANDAMENTO")).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/estadias").param("clienteId", String.valueOf(titular))).andExpect(jsonPath("$", hasSize(1)));
        pagar(estadiaId, "300.00");
        mvc.perform(post("/estadias/" + estadiaId + "/checkout")).andExpect(status().isOk());
        mvc.perform(get("/estadias").param("status", "EM_ANDAMENTO")).andExpect(jsonPath("$", hasSize(0)));
        mvc.perform(get("/estadias").param("status", "ENCERRADA")).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/estadias/999")).andExpect(status().isNotFound());
    }
}
