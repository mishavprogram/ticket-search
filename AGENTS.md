# Ticket Search - Agent Notes

Small local Spring Boot 4 app for practical Lucene search.

Current stack:
- Java 21, Maven, Spring Boot 4
- Thymeleaf UI
- SQLite + JPA
- Apache Lucene

Current features:
- Ticket search
- Email message search
- Generated email IDs
- Home navigation page
- Lucene search across title, description, importantWords

Search behavior:
- importantWords has highest priority
- title has medium priority
- description has lowest priority
- Boosts are configurable from the UI
- Search uses PrefixQuery + FuzzyQuery
- Short words are ignored unless in config/allowed-short-words.txt
- minScoreRatio default is 0.0

Current priorities:
1. Stabilize workflow
2. Keywords cleanup
3. Edit page for keywords
4. Later: video transcript MVP

Principles:
- Keep UX simple
- Do not remove or discourage email functionality
- Prefer small, safe steps
- Prefer simple architecture over overengineering
- Avoid heavy NLP unless explicitly requested
- Add or update tests for behavior changes
- Run `mvnw.cmd test` before finishing when practical
- Commit regularly when requested
