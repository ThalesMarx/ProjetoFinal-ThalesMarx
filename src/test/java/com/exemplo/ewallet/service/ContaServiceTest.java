package com.exemplo.ewallet.service;

import com.exemplo.ewallet.exception.SaldoInsuficienteException;
import com.exemplo.ewallet.model.Conta;
import com.exemplo.ewallet.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ContaService contaService;

    private Conta contaOrigem;
    private Conta contaDestino;

    @BeforeEach
    void setUp() {
        contaOrigem = new Conta(1L, "Alice", 500.0);
        contaDestino = new Conta(2L, "Bob", 200.0);
    }

    @Test
    void deveConsultarSaldoComSucesso() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(contaOrigem));

        Double saldo = contaService.consultarSaldo(1L);

        assertEquals(500.0, saldo);
        verify(contaRepository, times(1)).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoConsultarContaInexistente() {
        when(contaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> contaService.consultarSaldo(99L));
    }

    @Test
    void deveRealizarDepositoComSucesso() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(contaOrigem));
        when(contaRepository.save(any(Conta.class))).thenAnswer(i -> i.getArgument(0));

        Conta atualizada = contaService.realizarDeposito(1L, 150.0);

        assertEquals(650.0, atualizada.getSaldo());
        verify(contaRepository).save(contaOrigem);
    }

    @Test
    void deveLancarExcecaoAoDepositarValorZeroOuNegativo() {
        assertThrows(IllegalArgumentException.class, () -> contaService.realizarDeposito(1L, 0.0));
        assertThrows(IllegalArgumentException.class, () -> contaService.realizarDeposito(1L, -50.0));
        verify(contaRepository, never()).save(any());
    }

    @Test
    void deveRealizarTransferenciaComSucesso() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(contaOrigem));
        when(contaRepository.findById(2L)).thenReturn(Optional.of(contaDestino));

        contaService.realizarTransferencia(1L, 2L, 300.0);

        assertEquals(200.0, contaOrigem.getSaldo());
        assertEquals(500.0, contaDestino.getSaldo());
        verify(contaRepository, times(2)).save(any(Conta.class));
    }

    @Test
    void deveLancarSaldoInsuficienteExceptionNaTransferencia() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(contaOrigem));
        when(contaRepository.findById(2L)).thenReturn(Optional.of(contaDestino));

        assertThrows(SaldoInsuficienteException.class,
                () -> contaService.realizarTransferencia(1L, 2L, 600.0));

        assertEquals(500.0, contaOrigem.getSaldo());
        verify(contaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoTransferirValorInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> contaService.realizarTransferencia(1L, 2L, -10.0));
    }
}
