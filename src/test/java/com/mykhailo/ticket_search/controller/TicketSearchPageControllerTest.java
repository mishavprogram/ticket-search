package com.mykhailo.ticket_search.controller;

import com.mykhailo.ticket_search.model.TicketEntity;
import com.mykhailo.ticket_search.repository.TicketJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:sqlite:target/test-tickets.db",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class TicketSearchPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TicketJpaRepository ticketJpaRepository;

    @BeforeEach
    void cleanDatabase() {
        ticketJpaRepository.deleteAll();
    }

    @Test
    void shouldOpenHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldOpenAddTicketPage() throws Exception {
        mockMvc.perform(get("/add-ticket"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldOpenAddEmailMessagePage() throws Exception {
        mockMvc.perform(get("/add-email-message"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldOpenSearchPage() throws Exception {
        mockMvc.perform(get("/search-ui")
                        .param("text", "test"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldOpenTicketsMissingImportantWordsPage() throws Exception {
        mockMvc.perform(get("/tickets/missing-important-words")
                        .param("originalWebSite", "https://example.com/ticket/"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateOnlyImportantWords() throws Exception {
        ticketJpaRepository.save(new TicketEntity(
                "TEST-EDIT",
                "Original title",
                "Original description",
                LocalDate.of(2026, 1, 1),
                ""
        ));

        mockMvc.perform(post("/tickets/TEST-EDIT/edit")
                        .param("importantWords", "alpha beta"))
                .andExpect(status().is3xxRedirection());

        TicketEntity updated = ticketJpaRepository.findByNumber("TEST-EDIT")
                .orElseThrow();

        assertEquals("Original title", updated.getTitle());
        assertEquals("Original description", updated.getDescription());
        assertEquals(LocalDate.of(2026, 1, 1), updated.getClosedDate());
        assertEquals("alpha beta", updated.getImportantWords());
    }
}