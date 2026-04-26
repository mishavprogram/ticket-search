package com.mykhailo.ticket_search.service;

import com.mykhailo.ticket_search.model.Ticket;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TicketRepository {

    public List<Ticket> findAll() {
        return List.of(
                new Ticket("HD-1", "Login problem", "User cannot login to CRM after password change"),
                new Ticket("HD-2", "Schedule issue", "Teacher cannot see lesson in schedule"),
                new Ticket("HD-3", "Homework problem", "Student cannot upload homework file"),
                new Ticket("HD-4", "CRM error", "Page shows error after saving student profile"),
                new Ticket("HD-5", "Payment issue", "Parent does not see payment in account"),
                new Ticket("HD-6", "Group problem", "Cannot add student to group"),
                new Ticket("HD-7", "Video lesson issue", "Student cannot join online lesson"),
                new Ticket("HD-8", "Browser problem", "Site works incorrectly in Safari but works in Chrome"),
                new Ticket("HD-9", "Teacher account", "Teacher cannot open student profile in logbook"),
                new Ticket("HD-10", "Homework missing", "Homework file was not attached to lesson")
        );
    }
}
