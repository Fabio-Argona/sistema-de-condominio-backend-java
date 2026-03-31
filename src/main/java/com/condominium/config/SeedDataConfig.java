package com.condominium.config;

import com.condominium.model.AreaComum;
import com.condominium.repository.AreaComumRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SeedDataConfig {

    @Bean
    public CommandLineRunner initDatabase(AreaComumRepository areaComumRepository) {
        return args -> {
            // Busca ou cria a churrasqueira
            AreaComum churrasqueira = areaComumRepository.findAll().stream()
                    .filter(a -> "Churrasqueira".equals(a.getNome()))
                    .findFirst()
                    .orElse(new AreaComum());

            churrasqueira.setNome("Churrasqueira");
            churrasqueira.setDescricao("Espaço para confraternizações com churrasqueira, suportando até 20 pessoas. Caso o local seja entregue sujo ou desorganizado, será cobrada uma taxa adicional de R$ 100,00.");
            churrasqueira.setCapacidade(20);
            churrasqueira.setValorReserva(0.0);
            churrasqueira.setHorarioAbertura("10:00");
            churrasqueira.setHorarioFechamento("22:00");
            churrasqueira.setDisponivel(true);

            areaComumRepository.save(churrasqueira);
            System.out.println("✅ Churrasqueira atualizada no banco de dados.");
        };
    }
}
