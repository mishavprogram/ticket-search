package com.mykhailo.ticket_search.model;

import java.time.LocalDate;

public record Ticket(
        String number,
        String title,
        String description,
        LocalDate closedDate,
        String importantWords
) {
}