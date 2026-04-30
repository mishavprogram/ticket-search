package com.mykhailo.ticket_search.config;

public record SearchSettings(
        int maxResults,
        int maxEdits,
        int minWordLength,
        float minScoreRatio
) {

    public static SearchSettings defaultSettings() {
        return new SearchSettings(
                5,
                2,
                3,
                0.5f
        );
    }
}