package com.mykhailo.ticket_search.repository;

import com.mykhailo.ticket_search.model.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketJpaRepository extends JpaRepository<TicketEntity, Long> {
}