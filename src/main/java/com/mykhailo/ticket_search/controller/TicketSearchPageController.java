package com.mykhailo.ticket_search.controller;

import com.mykhailo.ticket_search.config.SearchSettings;
import com.mykhailo.ticket_search.model.TicketEntity;
import com.mykhailo.ticket_search.model.TicketSearchResult;
import com.mykhailo.ticket_search.repository.TicketJpaRepository;
import com.mykhailo.ticket_search.service.TicketSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        return "home";
    }

    @GetMapping("/search-ui")
    public String search(
            @RequestParam String text,
            @RequestParam(defaultValue = "5") int maxResults,
            @RequestParam(defaultValue = "2") int maxEdits,
            @RequestParam(defaultValue = "3") int minWordLength,
            @RequestParam(defaultValue = "0.0") float minScoreRatio,//TODO find correct default value
            @RequestParam(defaultValue = "") String originalWebSite,

            @RequestParam(defaultValue = "3.0") float importantWordsBoost,
            @RequestParam(defaultValue = "2.0") float titleBoost,
            @RequestParam(defaultValue = "1.0") float descriptionBoost,
            Model model
    ) throws Exception {
        SearchSettings settings = SearchSettings.of(
                maxResults,
                maxEdits,
                minWordLength,
                minScoreRatio,

                importantWordsBoost,
                titleBoost,
                descriptionBoost
        );

        List<TicketSearchResult> results = ticketSearchService.search(text, settings);

        List<UiSearchResult> uiResults = results.stream()
                .map(result -> new UiSearchResult(
                        result,
                        buildTicketUrl(originalWebSite, result.ticket().number())
                ))
                .toList();

        model.addAttribute("query", text);
        model.addAttribute("results", uiResults);
        model.addAttribute("settings", settings);
        model.addAttribute("originalWebSite", originalWebSite);

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
            @RequestParam LocalDate closedDate,
            @RequestParam(required = false) String importantWords
    ) {
        if (ticketJpaRepository.existsByNumber(number)) {
            return "redirect:/add-ticket?error=duplicate";
        }

        ticketJpaRepository.save(new TicketEntity(
                number,
                title,
                description,
                closedDate,
                importantWords != null ? importantWords : ""
        ));

        return "redirect:/add-ticket?success=true";
    }

    @GetMapping("/tickets/{number}/edit")
    public String editTicketPage(
            @PathVariable String number,
            @RequestParam(defaultValue = "") String originalWebSite,
            Model model
    ) {
        TicketEntity ticket = ticketJpaRepository.findByNumber(number)
                .orElseThrow();

        model.addAttribute("ticket", ticket);
        model.addAttribute("originalWebSite", originalWebSite);

        return "edit-ticket";
    }

    @GetMapping("/tickets/missing-important-words")
    public String ticketsMissingImportantWords(Model model, @RequestParam(defaultValue = "") String originalWebSite) {

        List<TicketEntity> tickets = ticketJpaRepository.findAll().stream()
                .filter(ticket ->
                        ticket.getImportantWords() == null//TODO maybe we dont need null and blank check in future
                                || ticket.getImportantWords().isBlank()
                )
                .toList();

        model.addAttribute("tickets", tickets);
        model.addAttribute("originalWebSite", originalWebSite);

        return "tickets-missing-important-words";
    }

    @PostMapping("/tickets/{number}/edit")
    public String updateImportantWords(
            @PathVariable String number,
            @RequestParam String importantWords,
            @RequestParam(defaultValue = "") String originalWebSite
    ) {

        TicketEntity ticket = ticketJpaRepository.findByNumber(number)
                .orElseThrow();

        ticket.setImportantWords(importantWords);

        ticketJpaRepository.save(ticket);

        return "redirect:/tickets/missing-important-words?originalWebSite=" + originalWebSite;
    }

    @GetMapping("/add-email-message")
    public String addEmailMessagePage() {
        return "add-email-message";
    }

    @PostMapping("/add-email-message")
    public String addEmailMessage(
            @RequestParam String number,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam LocalDate closedDate,
            @RequestParam(required = false) String importantWords
    ) {
        if (ticketJpaRepository.existsByNumber(number)) {
            return "redirect:/add-email-message?error=duplicate";
        }

        ticketJpaRepository.save(new TicketEntity(
                number,
                title,
                description,
                closedDate,
                importantWords != null ? importantWords : ""
        ));

        return "redirect:/add-email-message?success=true";
    }

    private String buildTicketUrl(String originalWebSite, String number) {
        if (originalWebSite == null || originalWebSite.isBlank()) {
            return "";
        }

        return originalWebSite + number;
    }

    public record UiSearchResult(
            TicketSearchResult result,
            String url
    ) {
    }
}