package com.mykhailo.ticket_search.config;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AllowedShortWordsProvider {

    private static final Path FILE_PATH = Path.of("config/allowed-short-words.txt");

    public Set<String> getAllowedShortWords() {
        ensureFileExists();

        try {
            return Files.readAllLines(FILE_PATH)
                    .stream()
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .filter(word -> !word.isBlank())
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            return defaultWords();
        }
    }

    private void ensureFileExists() {
        try {
            if (!Files.exists(FILE_PATH)) {
                Files.write(FILE_PATH, defaultWords().stream().toList());
            }
        } catch (IOException ignored) {
        }
    }

    private Set<String> defaultWords() {
        return Set.of("ai", "ui", "id", "sql", "crm", "api", "db");
    }
}