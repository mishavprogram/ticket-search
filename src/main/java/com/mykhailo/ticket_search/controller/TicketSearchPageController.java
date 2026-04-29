package com.mykhailo.ticket_search.controller;

import com.mykhailo.ticket_search.model.TicketSearchResult;
import com.mykhailo.ticket_search.service.TicketSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class TicketSearchPageController {

    private final TicketSearchService ticketSearchService;

    public TicketSearchPageController(TicketSearchService ticketSearchService) {
        this.ticketSearchService = ticketSearchService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/search-ui")
    public String search(@RequestParam String text, Model model) throws Exception {
        List<TicketSearchResult> results = ticketSearchService.search(text);

        model.addAttribute("query", text);
        model.addAttribute("results", results);

        return "index";
    }
}