package com.mykhailo.ticket_search.service;

import com.mykhailo.ticket_search.config.SearchSettings;
import com.mykhailo.ticket_search.model.TicketEntity;
import com.mykhailo.ticket_search.model.TicketSearchResult;
import com.mykhailo.ticket_search.repository.TicketJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:sqlite:target/test-tickets.db",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class TicketSearchRealDataTest {

    @Autowired
    private TicketSearchService ticketSearchService;

    @Autowired
    private TicketJpaRepository ticketJpaRepository;

    @BeforeEach
    void cleanDatabase() {
        ticketJpaRepository.deleteAll();
    }

    @Test
    void shouldFindByUniqueImportantWordOnly() throws Exception {
        saveRealisticTestTickets();

        List<TicketSearchResult> results = ticketSearchService.search(
                "ремонт",
                SearchSettings.defaultSettings()
        );

        assertFalse(results.isEmpty());
        assertEquals("3", results.get(0).ticket().number());
        assertEquals("Штукатурка стіни", results.get(0).ticket().title());
        assertEquals("ремонт", results.get(0).ticket().importantWords());
    }

    @Test
    void shouldFindByDescriptionOnly() throws Exception {
        saveRealisticTestTickets();

        List<TicketSearchResult> results = ticketSearchService.search(
                "transaction",
                SearchSettings.defaultSettings()
        );

        assertFalse(results.isEmpty());
        assertEquals("4", results.get(0).ticket().number());
        assertEquals("Проблема з оплатою", results.get(0).ticket().title());
    }

    @Test
    void shouldRankFpsTicketHigherThanOverheating() throws Exception {
        saveRealisticTestTickets();

        List<TicketSearchResult> results = ticketSearchService.search(
                "fps ноут",
                SearchSettings.defaultSettings()
        );

        assertFalse(results.isEmpty());

        // main check
        assertEquals("5", results.get(0).ticket().number());

        // stronger check (optional but good)
        List<String> numbers = results.stream()
                .map(r -> r.ticket().number())
                .toList();

        assertTrue(numbers.indexOf("5") < numbers.indexOf("1"));
        assertTrue(numbers.indexOf("5") < numbers.indexOf("2"));
    }

    @Test
    void shouldIgnoreShortWordsEvenIfAnotherTicketContainsThemManyTimes() throws Exception {
        saveRealisticTestTickets();

        List<TicketSearchResult> results = ticketSearchService.search(
                "на ноут",
                SearchSettings.defaultSettings()
        );

        assertFalse(results.isEmpty());

        List<String> numbers = results.stream()
                .map(r -> r.ticket().number())
                .toList();

        assertEquals("5", results.get(0).ticket().number());
        assertFalse(numbers.contains("6"));
    }

    @Test
    void shouldReturnNoResultsForIgnoredShortWordOnly() throws Exception {
        saveRealisticTestTickets();

        List<TicketSearchResult> results = ticketSearchService.search(
                "на",
                SearchSettings.defaultSettings()
        );

        assertTrue(results.isEmpty());
    }

    @Test
    void shouldFindFuzzyMatchInRealData() throws Exception {
        saveRealisticTestTickets();

        SearchSettings settings = SearchSettings.of(
                5,      // maxResults
                1,      // maxEdits: allows "температураа" -> "температура"
                3,      // minWordLength
                0.0f,   // minScoreRatio
                3.0f,   // importantWordsBoost
                2.0f,   // titleBoost
                1.0f    // descriptionBoost
        );

        List<TicketSearchResult> results = ticketSearchService.search(
                "температураа",
                settings
        );

        assertFalse(results.isEmpty());

        List<String> numbers = results.stream()
                .map(r -> r.ticket().number())
                .toList();

        assertTrue(numbers.contains("1"));
        assertTrue(numbers.contains("2"));
        assertFalse(numbers.contains("3"));
    }

    @Test
    void shouldHandleFuzzyTypoInsideWord() throws Exception {
        saveRealisticTestTickets();

        List<TicketSearchResult> results = ticketSearchService.search(
                "темпаратура", // typo in middle
                SearchSettings.defaultSettings()
        );

        assertFalse(results.isEmpty());
    }

    @Test
    void shouldFindByPrefix() throws Exception {
        saveRealisticTestTickets();

        List<TicketSearchResult> results = ticketSearchService.search(
                "темпер", // prefix of "температура"
                SearchSettings.defaultSettings()
        );

        assertFalse(results.isEmpty());

        List<String> numbers = results.stream()
                .map(r -> r.ticket().number())
                .toList();

        assertTrue(numbers.contains("1"));
        assertTrue(numbers.contains("2"));
    }

    private void saveRealisticTestTickets() {
        ticketJpaRepository.save(new TicketEntity(
                "1",
                "Ноутбук перегрівається",
                "Мій ноутбук Lenovo Legion 5 у відеоіграх показує температури 100-105 градусів.",
                LocalDate.of(2026, 4, 5),
                "ноутбук перегрів температура gpu"
        ));

        ticketJpaRepository.save(new TicketEntity(
                "2",
                "ПК греется",
                "Ноут в играх показывает 100 градусов.",
                LocalDate.of(2026, 4, 6),
                "температура ноут cpu gpu"
        ));

        ticketJpaRepository.save(new TicketEntity(
                "3",
                "Штукатурка стіни",
                "Роблю штукатурку стіни, але не вистачає матеріалів.",
                LocalDate.of(2026, 4, 6),
                "ремонт"
        ));

        ticketJpaRepository.save(new TicketEntity(
                "4",
                "Проблема з оплатою",
                "Користувач бачить помилку transaction failed після платежу.",
                LocalDate.of(2026, 4, 7),
                "платіж банк"
        ));

        ticketJpaRepository.save(new TicketEntity(
                "5",
                "Ноут лагає в іграх",
                "У грі низький FPS, але температура нормальна.",
                LocalDate.of(2026, 4, 8),
                "ноут fps лаги продуктивність"
        ));

        ticketJpaRepository.save(new TicketEntity(
                "6",
                "Слово на багато разів",
                "на на на на на на на на на на",
                LocalDate.of(2026, 4, 9),
                ""
        ));
    }
}