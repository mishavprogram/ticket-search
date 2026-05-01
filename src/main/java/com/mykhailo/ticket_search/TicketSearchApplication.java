package com.mykhailo.ticket_search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
public class TicketSearchApplication {

    public static void main(String[] args) {
        try {
            Files.createDirectories(Path.of("data"));
            Files.createDirectories(Path.of("config"));
        } catch (IOException ignored) {
        }

        SpringApplication.run(TicketSearchApplication.class, args);
    }
}
