package com.mykhailo.ticket_search.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:sqlite:target/test-tickets.db",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class TicketSearchPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
}