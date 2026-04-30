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

    public static SearchSettings of(
            int maxResults,
            int maxEdits,
            int minWordLength,
            float minScoreRatio
    ) {
        return new SearchSettings(
                Math.clamp(maxResults, 1, 20),
                Math.clamp(maxEdits, 0, 2),
                Math.clamp(minWordLength, 0, 10),
                Math.clamp(minScoreRatio, 0.0f, 1.0f)
        );
    }
}