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
class ContaControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    RelogioDeTeste relogio;

    private long quarto;
    private long estadia;   // 2 diárias de R$ 300,00 = R$ 600,00
    private long agua;      // R$ 8,50
    private long lavanderia; // R$ 45,00

    private long criar(String url, String json) throws Exception {
        String body = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    @BeforeEach
    void dados() throws Exception {
        relogio.reiniciar();
        LocalDate hoje = LocalDate.now(relogio);
        long cliente = criar("/clientes", """
                {"nome":"Ana","sobrenome":"Silva","cpf":"12345678901","nacionalidade":"Brasileira"}""");
        long tipo = criar("/tipos-quarto", "{\"nome\":\"Casal\",\"capacidade\":2,\"tarifaBase\":300.00}");
        quarto = criar("/quartos", "{\"numero\":\"101\",\"andar\":1,\"tipoQuartoId\":" + tipo + "}");
        long reserva = criar("/reservas", """
                {"clienteId":%d,"quartoId":%d,"dataEntrada":"%s","dataSaida":"%s","quantidadeHospedes":1}"""
                .formatted(cliente, quarto, hoje, hoje.plusDays(2)));
        mvc.perform(patch("/reservas/" + reserva + "/confirmar")).andExpect(status().isOk());
        estadia = criar("/estadias/checkin", "{\"reservaId\":" + reserva + "}");
        agua = criar("/produtos", "{\"nome\":\"Água mineral 500ml\",\"categoria\":\"FRIGOBAR\",\"preco\":8.50}");
        lavanderia = criar("/produtos", "{\"nome\":\"Lavagem de camisa\",\"categoria\":\"LAVANDERIA\",\"preco\":45.00}");
    }

    private ResultActions consumo(long produto, int quantidade) throws Exception {
        return mvc.perform(post("/estadias/" + estadia + "/consumos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"produtoId\":%d,\"quantidade\":%d}".formatted(produto, quantidade)));
    }

    private ResultActions pagamento(String forma, String valor) throws Exception {
        return mvc.perform(post("/estadias/" + estadia + "/pagamentos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"forma\":\"%s\",\"valor\":%s}".formatted(forma, valor)));
    }

    private ResultActions estorno(String url, String motivo) throws Exception {
        return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON)
                .content(motivo == null ? "{}" : "{\"motivo\":\"" + motivo + "\"}"));
    }

    private long idDe(ResultActions r) throws Exception {
        return ((Number) JsonPath.read(r.andReturn().getResponse().getContentAsString(), "$.id")).longValue();
    }

    private ResultActions conta() throws Exception {
        return mvc.perform(get("/estadias/" + estadia + "/conta"));
    }

    @Test
    void contaInicialTemApenasAHospedagem() throws Exception {
        conta().andExpect(status().isOk())
                .andExpect(jsonPath("$.statusEstadia").value("EM_ANDAMENTO"))
                .andExpect(jsonPath("$.contaFechada").value(false))
                .andExpect(jsonPath("$.valorHospedagem").value(600.00))
                .andExpect(jsonPath("$.consumos", hasSize(0)))
                .andExpect(jsonPath("$.totalConsumos").value(0))
                .andExpect(jsonPath("$.totalConta").value(600.00))
                .andExpect(jsonPath("$.saldo").value(600.00));
    }

    @Test
    void lancaConsumosNaConta() throws Exception {
        consumo(agua, 2).andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Água mineral 500ml"))
                .andExpect(jsonPath("$.categoria").value("FRIGOBAR"))
                .andExpect(jsonPath("$.valorUnitario").value(8.50))
                .andExpect(jsonPath("$.valorTotal").value(17.00))
                .andExpect(jsonPath("$.estornado").value(false));
        consumo(lavanderia, 1).andExpect(status().isCreated());

        conta().andExpect(jsonPath("$.consumos", hasSize(2)))
                .andExpect(jsonPath("$.totalConsumos").value(62.00))
                .andExpect(jsonPath("$.totalConta").value(662.00))
                .andExpect(jsonPath("$.saldo").value(662.00));
    }

    @Test
    void precoFicaCongeladoNoLancamento() throws Exception {
        consumo(agua, 1).andExpect(status().isCreated());
        mvc.perform(put("/produtos/" + agua).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Água mineral 500ml\",\"categoria\":\"FRIGOBAR\",\"preco\":12.00}"))
                .andExpect(status().isOk());

        conta().andExpect(jsonPath("$.consumos[0].valorUnitario").value(8.50))
                .andExpect(jsonPath("$.totalConsumos").value(8.50));
    }

    @Test
    void estornoDeConsumoSaiDoTotalMasFicaNoHistorico() throws Exception {
        long id = idDe(consumo(lavanderia, 1));

        estorno("/estadias/" + estadia + "/consumos/" + id + "/estorno", null)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.motivo").exists());
        estorno("/estadias/" + estadia + "/consumos/" + id + "/estorno", "Lançado no quarto errado")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estornado").value(true))
                .andExpect(jsonPath("$.motivoEstorno").value("Lançado no quarto errado"));
        estorno("/estadias/" + estadia + "/consumos/" + id + "/estorno", "De novo")
                .andExpect(status().isConflict());

        conta().andExpect(jsonPath("$.consumos", hasSize(1)))
                .andExpect(jsonPath("$.consumos[0].estornado").value(true))
                .andExpect(jsonPath("$.totalConsumos").value(0))
                .andExpect(jsonPath("$.saldo").value(600.00));
    }

    @Test
    void pagamentosReduzemOSaldoEPodemSerEstornados() throws Exception {
        pagamento("PIX", "200.00").andExpect(status().isCreated())
                .andExpect(jsonPath("$.forma").value("PIX"))
                .andExpect(jsonPath("$.pagoEm").exists());
        long cartao = idDe(pagamento("CARTAO_CREDITO", "150.00"));

        conta().andExpect(jsonPath("$.totalPagamentos").value(350.00))
                .andExpect(jsonPath("$.saldo").value(250.00));

        estorno("/estadias/" + estadia + "/pagamentos/" + cartao + "/estorno", "Cartão recusado")
                .andExpect(status().isOk());
        conta().andExpect(jsonPath("$.totalPagamentos").value(200.00))
                .andExpect(jsonPath("$.saldo").value(400.00));
    }

    @Test
    void validaPagamento() throws Exception {
        pagamento("PIX", "0").andExpect(status().isBadRequest()).andExpect(jsonPath("$.erros.valor").exists());
        pagamento("CHEQUE", "10").andExpect(status().isBadRequest());
        mvc.perform(post("/estadias/" + estadia + "/pagamentos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.forma").exists());
    }

    @Test
    void checkoutExigeContaQuitadaSemAlterarNada() throws Exception {
        consumo(agua, 2).andExpect(status().isCreated());
        relogio.avancarDias(2);
        pagamento("DINHEIRO", "100.00").andExpect(status().isCreated());

        mvc.perform(post("/estadias/" + estadia + "/checkout"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value(
                        "Conta com saldo pendente de R$ 517,00; registre o pagamento antes do check-out"));

        // Nada mudou: estadia segue aberta e o quarto ocupado
        mvc.perform(get("/estadias/" + estadia)).andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));
        mvc.perform(get("/quartos/" + quarto)).andExpect(jsonPath("$.status").value("OCUPADO"));

        pagamento("PIX", "517.00").andExpect(status().isCreated());
        mvc.perform(post("/estadias/" + estadia + "/checkout")).andExpect(status().isOk());

        conta().andExpect(jsonPath("$.contaFechada").value(true))
                .andExpect(jsonPath("$.saldo").value(0));
    }

    @Test
    void contaFechadaNaoAceitaLancamentos() throws Exception {
        long pago = idDe(pagamento("PIX", "300.00"));
        mvc.perform(post("/estadias/" + estadia + "/checkout")).andExpect(status().isOk()); // 1 diária

        consumo(agua, 1).andExpect(status().isConflict());
        pagamento("PIX", "10.00").andExpect(status().isConflict());
        estorno("/estadias/" + estadia + "/pagamentos/" + pago + "/estorno", "Teste").andExpect(status().isConflict());
    }

    @Test
    void saidaAntecipadaComPagamentoTotalGeraSaldoADevolver() throws Exception {
        pagamento("CARTAO_CREDITO", "600.00").andExpect(status().isCreated());
        mvc.perform(post("/estadias/" + estadia + "/checkout")).andExpect(status().isOk()); // usou 1 de 2 diárias

        conta().andExpect(jsonPath("$.valorHospedagem").value(300.00))
                .andExpect(jsonPath("$.saldo").value(-300.00));
    }

    @Test
    void naoLancaProdutoInativoNemEmEstadiaInexistente() throws Exception {
        mvc.perform(put("/produtos/" + agua).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Água mineral 500ml\",\"categoria\":\"FRIGOBAR\",\"preco\":8.50,\"ativo\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));
        consumo(agua, 1).andExpect(status().isUnprocessableContent());
        consumo(999, 1).andExpect(status().isNotFound());

        mvc.perform(post("/estadias/999/consumos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"produtoId\":%d,\"quantidade\":1}".formatted(lavanderia)))
                .andExpect(status().isNotFound());
        mvc.perform(get("/estadias/999/conta")).andExpect(status().isNotFound());
        estorno("/estadias/" + estadia + "/consumos/999/estorno", "x").andExpect(status().isNotFound());
    }
}
