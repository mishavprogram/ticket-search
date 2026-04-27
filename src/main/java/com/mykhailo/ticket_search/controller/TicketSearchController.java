package com.mykhailo.ticket_search.controller;

import com.mykhailo.ticket_search.model.TicketSearchResult;
import com.mykhailo.ticket_search.service.TicketSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TicketSearchController {

    private final TicketSearchService ticketSearchService;

    public TicketSearchController(TicketSearchService ticketSearchService) {
        this.ticketSearchService = ticketSearchService;
    }

    @GetMapping("/search")
    public List<TicketSearchResult> search(String text) throws Exception {
        return ticketSearchService.search(text);
    }
}