package com.mykhailo.ticket_search.config;

public record SearchSettings(
        int maxResults,
        int maxEdits,
        int minWordLength,
        float minScoreRatio,

        float importantWordsBoost,
        float titleBoost,
        float descriptionBoost
) {

    public static SearchSettings defaultSettings() {
        return new SearchSettings(
                5,
                2,
                3,
                0.0f,

                3.0f,
                2.0f,
                1.0f
        );
    }

    public static SearchSettings of(
            int maxResults,
            int maxEdits,
            int minWordLength,
            float minScoreRatio,

            float importantWordsBoost,
            float titleBoost,
            float descriptionBoost
    ) {
        return new SearchSettings(
                Math.clamp(maxResults, 1, 20),
                Math.clamp(maxEdits, 0, 2),
                Math.clamp(minWordLength, 0, 10),
                Math.clamp(minScoreRatio, 0.0f, 1.0f),

                Math.clamp(importantWordsBoost, 0.1f, 10.0f),
                Math.clamp(titleBoost, 0.1f, 10.0f),
                Math.clamp(descriptionBoost, 0.1f, 10.0f)
        );
    }
}