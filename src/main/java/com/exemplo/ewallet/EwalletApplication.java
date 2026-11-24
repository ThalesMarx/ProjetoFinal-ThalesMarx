package com.exemplo.ewallet;

import com.exemplo.ewallet.model.Conta;
import com.exemplo.ewallet.repository.ContaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EwalletApplication {

    public static void main(String[] args) {
        SpringApplication.run(EwalletApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(ContaRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Conta(null, "Alice", 500.0));
                repository.save(new Conta(null, "Bob", 200.0));
            }
        };
    }
}
