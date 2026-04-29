package com.mykhailo.ticket_search.repository;

import com.mykhailo.ticket_search.model.Ticket;
import com.mykhailo.ticket_search.model.TicketEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TicketRepository {

    private final TicketJpaRepository ticketJpaRepository;

    public TicketRepository(TicketJpaRepository ticketJpaRepository) {
        this.ticketJpaRepository = ticketJpaRepository;
    }

    public List<Ticket> findAll() {
        return ticketJpaRepository.findAll()
                .stream()
                .map(this::toTicket)
                .toList();
    }

    private Ticket toTicket(TicketEntity entity) {
        return new Ticket(
                entity.getNumber(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getClosedDate()
        );
    }
}