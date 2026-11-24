package com.exemplo.ewallet.controller;

import com.exemplo.ewallet.model.Conta;
import com.exemplo.ewallet.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ContaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContaRepository contaRepository;

    private Conta conta1;
    private Conta conta2;

    @BeforeEach
    void setUp() {
        contaRepository.deleteAll();
        conta1 = contaRepository.save(new Conta(null, "Alice", 500.0));
        conta2 = contaRepository.save(new Conta(null, "Bob", 200.0));
    }

    @Test
    void deveRetornar200AoConsultarSaldo() throws Exception {
        mockMvc.perform(get("/api/contas/{id}/saldo", conta1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(500.0));
    }

    @Test
    void deveRetornar200AoDepositarValorValido() throws Exception {
        String json = "{\"valor\": 100.0}";

        mockMvc.perform(post("/api/contas/{id}/deposito", conta1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(600.0));
    }

    @Test
    void deveRetornar400AoDepositarValorNegativo() throws Exception {
        String json = "{\"valor\": -50.0}";

        mockMvc.perform(post("/api/contas/{id}/deposito", conta1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    void deveRetornar200AoTransferirComSaldoSuficiente() throws Exception {
        String json = String.format("{\"idOrigem\": %d, \"idDestino\": %d, \"valor\": 200.0}",
                conta1.getId(), conta2.getId());

        mockMvc.perform(post("/api/contas/transferencia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Transferência realizada com sucesso."));
    }

    @Test
    void deveRetornar400AoTransferirComSaldoInsuficiente() throws Exception {
        String json = String.format("{\"idOrigem\": %d, \"idDestino\": %d, \"valor\": 999.0}",
                conta1.getId(), conta2.getId());

        mockMvc.perform(post("/api/contas/transferencia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Saldo insuficiente na conta de origem."));
    }
}
