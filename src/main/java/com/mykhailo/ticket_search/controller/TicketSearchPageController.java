package com.mykhailo.ticket_search.controller;

import com.mykhailo.ticket_search.config.SearchSettings;
import com.mykhailo.ticket_search.model.TicketEntity;
import com.mykhailo.ticket_search.model.TicketSearchResult;
import com.mykhailo.ticket_search.repository.TicketJpaRepository;
import com.mykhailo.ticket_search.service.TicketSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class TicketSearchPageController {

    private final TicketSearchService ticketSearchService;
    private final TicketJpaRepository ticketJpaRepository;

    public TicketSearchPageController(TicketSearchService ticketSearchService, TicketJpaRepository ticketJpaRepository) {
        this.ticketSearchService = ticketSearchService;
        this.ticketJpaRepository = ticketJpaRepository;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/search-ui")
    public String search(
            @RequestParam String text,
            @RequestParam(defaultValue = "5") int maxResults,
            @RequestParam(defaultValue = "2") int maxEdits,
            @RequestParam(defaultValue = "3") int minWordLength,
            @RequestParam(defaultValue = "0.5") float minScoreRatio,
            Model model
    ) throws Exception {
        SearchSettings settings = SearchSettings.of(
                maxResults,
                maxEdits,
                minWordLength,
                minScoreRatio
        );

        List<TicketSearchResult> results = ticketSearchService.search(text, settings);

        model.addAttribute("query", text);
        model.addAttribute("results", results);
        model.addAttribute("settings", settings);

        return "index";
    }

    @GetMapping("/add-ticket")
    public String addTicketPage() {
        return "add-ticket";
    }

    @PostMapping("/add-ticket")
    public String addTicket(
            @RequestParam String number,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam LocalDate closedDate
    ) {

        if (ticketJpaRepository.existsByNumber(number)) {
            return "redirect:/add-ticket?error=duplicate";
        }

        ticketJpaRepository.save(new TicketEntity(
                number,
                title,
                description,
                closedDate
        ));

        return "redirect:/add-ticket?success=true";
    }
}