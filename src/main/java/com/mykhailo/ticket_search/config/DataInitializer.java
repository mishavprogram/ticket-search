package com.mykhailo.ticket_search.config;

import com.mykhailo.ticket_search.model.TicketEntity;
import com.mykhailo.ticket_search.repository.TicketJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TicketJpaRepository ticketJpaRepository;

    public DataInitializer(TicketJpaRepository ticketJpaRepository) {
        this.ticketJpaRepository = ticketJpaRepository;
    }

    @Override
    public void run(String... args) {
        if (ticketJpaRepository.count() > 0) {
            return;
        }

        ticketJpaRepository.save(new TicketEntity(
                "MVP-01",
                "Тестовий тікет",
                "Це перший тестовий тікет у базі SQLite.",
                LocalDate.of(2026, 4, 29)
        ));
    }
}
