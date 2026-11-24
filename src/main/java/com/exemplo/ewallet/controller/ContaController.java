package com.exemplo.ewallet.controller;

import com.exemplo.ewallet.model.Conta;
import com.exemplo.ewallet.service.ContaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping("/{id}/saldo")
    public ResponseEntity<Map<String, Double>> consultarSaldo(@PathVariable Long id) {
        Double saldo = contaService.consultarSaldo(id);
        return ResponseEntity.ok(Map.of("saldo", saldo));
    }

    @PostMapping("/{id}/deposito")
    public ResponseEntity<Conta> depositar(@PathVariable Long id, @RequestBody Map<String, Double> payload) {
        Double valor = payload.get("valor");
        Conta contaAtualizada = contaService.realizarDeposito(id, valor);
        return ResponseEntity.ok(contaAtualizada);
    }

    @PostMapping("/transferencia")
    public ResponseEntity<Map<String, String>> transferir(@RequestBody Map<String, Object> payload) {
        Long idOrigem = Long.valueOf(payload.get("idOrigem").toString());
        Long idDestino = Long.valueOf(payload.get("idDestino").toString());
        Double valor = Double.valueOf(payload.get("valor").toString());

        contaService.realizarTransferencia(idOrigem, idDestino, valor);
        return ResponseEntity.ok(Map.of("mensagem", "Transferência realizada com sucesso."));
    }
}
