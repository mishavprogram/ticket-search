package com.mykhailo.ticket_search.model;

public record TicketSearchResult(
        Ticket ticket,
        float score
) {
}