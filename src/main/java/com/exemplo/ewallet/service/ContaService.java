package com.exemplo.ewallet.service;

import com.exemplo.ewallet.exception.SaldoInsuficienteException;
import com.exemplo.ewallet.model.Conta;
import com.exemplo.ewallet.repository.ContaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContaService {

    private final ContaRepository contaRepository;

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    public Double consultarSaldo(Long id) {
        Conta conta = buscarContaPorId(id);
        return conta.getSaldo();
    }

    @Transactional
    public Conta realizarDeposito(Long id, Double valor) {
        if (valor == null || valor <= 0) {
            throw new IllegalArgumentException("O valor do depósito deve ser maior que zero.");
        }
        Conta conta = buscarContaPorId(id);
        conta.setSaldo(conta.getSaldo() + valor);
        return contaRepository.save(conta);
    }

    @Transactional
    public void realizarTransferencia(Long idOrigem, Long idDestino, Double valor) {
        if (valor == null || valor <= 0) {
            throw new IllegalArgumentException("O valor da transferência deve ser maior que zero.");
        }
        Conta origem = buscarContaPorId(idOrigem);
        Conta destino = buscarContaPorId(idDestino);

        if (origem.getSaldo() < valor) {
            throw new SaldoInsuficienteException("Saldo insuficiente na conta de origem.");
        }

        origem.setSaldo(origem.getSaldo() - valor);
        destino.setSaldo(destino.getSaldo() + valor);

        contaRepository.save(origem);
        contaRepository.save(destino);
    }

    private Conta buscarContaPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada: " + id));
    }
}
