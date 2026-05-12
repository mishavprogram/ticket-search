package com.mykhailo.ticket_search.repository;

import com.mykhailo.ticket_search.model.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketJpaRepository extends JpaRepository<TicketEntity, Long> {
    boolean existsByNumber(String number);
    Optional<TicketEntity> findByNumber(String number);
}