# Ticket Search — Agent Notes

Local Spring Boot app for searching tickets.

Goal: simple practical search, not complex architecture.

Current stack:
- Spring Boot
- SQLite
- Thymeleaf
- Apache Lucene

Search logic:
- Fields: title, description, importantWords
- importantWords has highest priority
- title has medium priority
- description has lowest priority
- Boosts are configurable from UI
- Search uses PrefixQuery + FuzzyQuery
- Short words are ignored unless in config/allowed-short-words.txt
- minScoreRatio default = 0.0

Principles:
- Keep UX simple
- Avoid heavy NLP
- Avoid complex architecture
- Prefer small safe changes
- Commit regularly