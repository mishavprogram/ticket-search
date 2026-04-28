package com.mykhailo.ticket_search.service;

import com.mykhailo.ticket_search.model.Ticket;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TicketRepository {

    public List<Ticket> findAll() {
        return List.of(
                new Ticket("HD-1", "Login problem", "User cannot login to CRM after password change", LocalDate.of(2026, 4, 28)),
                new Ticket("HD-2", "Schedule issue", "Teacher cannot see lesson in schedule", LocalDate.of(2026, 4, 25)),
                new Ticket("HD-3", "Homework problem", "Student cannot upload homework file", LocalDate.of(2026, 4, 20)),
                new Ticket("HD-4", "CRM error", "Page shows error after saving student profile", LocalDate.of(2026, 4, 15)),
                new Ticket("HD-5", "Payment issue", "Parent does not see payment in account", LocalDate.of(2026, 4, 10)),
                new Ticket("HD-6", "Group problem", "Cannot add student to group", LocalDate.of(2026, 4, 5)),
                new Ticket("HD-7", "Video lesson issue", "Student cannot join online lesson", LocalDate.of(2026, 3, 28)),
                new Ticket("HD-8", "Browser problem", "Site works incorrectly in Safari but works in Chrome", LocalDate.of(2026, 3, 20)),
                new Ticket("HD-9", "Teacher account", "Teacher cannot open student profile in logbook", LocalDate.of(2026, 3, 10)),
                new Ticket("HD-10", "Homework missing", "Homework file was not attached to lesson", LocalDate.of(2026, 2, 25))
        );
    }
}
