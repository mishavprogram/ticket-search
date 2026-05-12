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
- importantWords (db column) should be written manually in English
- Avoid duplicating keywords in UA/RU unless there is a real search need (config/allowed-short-words.txt)
- minScoreRatio default is 0.0

Current priorities:
1. Stabilize workflow
2. Keywords cleanup
3. Edit page for keywords
4. Later: video transcript MVP
5. Consider diagnostic page for allowed-short-words.txt usage:
   show which allowed short words are present in current DB data and which are unused.

Notes:
- allowed-short-words.txt is mainly intended for abbreviations, system names, and short technical/domain terms
- allowed-short-words.txt is mainly useful for words shorter than minWordLength
- words with length >= minWordLength are usually redundant in this file

Principles:
- Keep UX simple
- Do not remove or discourage email functionality
- Prefer small, safe steps
- Prefer simple architecture over overengineering
- Avoid heavy NLP unless explicitly requested
- Add or update tests for behavior changes
- Run `mvnw.cmd test` before finishing when practical
- Commit regularly when requested
