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

// TODO: Review and improve these tests.
// Current tests were created quickly for basic search behavior coverage.

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:sqlite:target/test-tickets.db",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class TicketSearchServiceTest {

    @Autowired
    private TicketSearchService ticketSearchService;

    @Autowired
    private TicketJpaRepository ticketJpaRepository;

    @BeforeEach
    void cleanDatabase() {
        ticketJpaRepository.deleteAll();
    }

    @Test
    void shouldReturnOverheatingTicketFirst() throws Exception {
        saveTicket(
                "TEST-01",
                "Ноут гріється",
                "Після заміни термопасти ноут знову гріється. Треба перевірити термопрокладки."
        );

        saveTicket(
                "TEST-02",
                "Новий ноут",
                "Хочу купити новий ноут Lenovo Legion для ігор."
        );

        List<TicketSearchResult> results = ticketSearchService.search(
                "ноут гріється термопаста",
                SearchSettings.defaultSettings()
        );

        assertFalse(results.isEmpty());
        assertEquals("TEST-01", results.get(0).ticket().number());
    }

    @Test
    void shouldFindFuzzyMatch() throws Exception {
        saveTicket(
                "TEST-01",
                "Заміна термопасти",
                "Ноут гріється, потрібна заміна термопасти."
        );

        List<TicketSearchResult> results = ticketSearchService.search(
                "термопастаа",
                SearchSettings.defaultSettings()
        );

        assertFalse(results.isEmpty());
        assertEquals("TEST-01", results.get(0).ticket().number());
    }

    @Test
    void shouldLimitResultsBySettings() throws Exception {
        saveTicket("TEST-01", "Ноут гріється", "Ноут гріється під час гри.");
        saveTicket("TEST-02", "Новий ноут", "Потрібен новий ноут для ігор.");
        saveTicket("TEST-03", "Апгрейд ноута", "Хочу апгрейд ноута для FPS.");

        SearchSettings settings = SearchSettings.of(
                1,
                2,
                3,
                0.0f
        );

        List<TicketSearchResult> results = ticketSearchService.search(
                "ноут",
                settings
        );

        assertTrue(results.size() <= 1);
    }

    @Test
    void shouldFilterWeakResultsByScoreRatio() throws Exception {
        saveTicket(
                "TEST-01",
                "Ноут гріється",
                "Ноут гріється після заміни термопасти. Треба перевірити термопрокладки."
        );

        saveTicket(
                "TEST-02",
                "Новий ноут",
                "Хочу купити новий ноут Lenovo Legion."
        );

        SearchSettings settings = SearchSettings.of(
                5,
                2,
                3,
                0.5f
        );

        List<TicketSearchResult> results = ticketSearchService.search(
                "ноут гріється термопаста термопрокладки",
                settings
        );

        assertFalse(results.isEmpty());
        assertEquals("TEST-01", results.get(0).ticket().number());
    }

    private void saveTicket(String number, String title, String description) {
        ticketJpaRepository.save(new TicketEntity(
                number,
                title,
                description,
                LocalDate.of(2026, 1, 1)
        ));
    }
}