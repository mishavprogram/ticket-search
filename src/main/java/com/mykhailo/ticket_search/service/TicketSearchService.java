package com.mykhailo.ticket_search.service;

import com.mykhailo.ticket_search.config.AllowedShortWordsProvider;
import com.mykhailo.ticket_search.config.SearchSettings;
import com.mykhailo.ticket_search.model.Ticket;
import com.mykhailo.ticket_search.model.TicketSearchResult;
import com.mykhailo.ticket_search.repository.TicketRepository;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketSearchService {

    private static final String FIELD_NUMBER = "number";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_CLOSED_DATE = "closedDate";
    private static final String FIELD_IMPORTANT = "importantWords";

    private final TicketRepository ticketRepository;
    private final AllowedShortWordsProvider allowedShortWordsProvider;

    public TicketSearchService(TicketRepository ticketRepository, AllowedShortWordsProvider allowedShortWordsProvider) {
        this.ticketRepository = ticketRepository;
        this.allowedShortWordsProvider = allowedShortWordsProvider;
    }

    public List<TicketSearchResult> search(String text, SearchSettings settings) throws Exception {
        Analyzer analyzer = new StandardAnalyzer();
        ByteBuffersDirectory index = new ByteBuffersDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        createIndex(index, config);

        return findTickets(text, index, settings);
    }

    public List<TicketSearchResult> search(String text) throws Exception {
        return search(text, SearchSettings.defaultSettings());
    }

    private List<TicketSearchResult> findTickets(String text, ByteBuffersDirectory index, SearchSettings settings) throws IOException {
        try (DirectoryReader reader = DirectoryReader.open(index)) {
            IndexSearcher searcher = new IndexSearcher(reader);

            BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

            String[] words = text.toLowerCase().split("\\s+");

            buildSearchQuery(words, queryBuilder, settings);

            Query query = queryBuilder.build();

            TopDocs results = searcher.search(query, settings.maxResults());

            List<TicketSearchResult> foundTickets = new ArrayList<>();

            collectFoundTickets(results, searcher, foundTickets, settings);

            return foundTickets;
        }
    }

    private void collectFoundTickets(
            TopDocs results,
            IndexSearcher searcher,
            List<TicketSearchResult> foundTickets,
            SearchSettings settings
    ) throws IOException {
        if (results.scoreDocs.length == 0) {
            return;
        }

        float topScore = results.scoreDocs[0].score;
        float minAllowedScore = topScore * settings.minScoreRatio();

        for (ScoreDoc scoreDoc : results.scoreDocs) {
            if (scoreDoc.score < minAllowedScore) {
                continue;
            }

            Document doc = searcher.doc(scoreDoc.doc);

            Ticket ticket = new Ticket(
                    doc.get(FIELD_NUMBER),
                    doc.get(FIELD_TITLE),
                    doc.get(FIELD_DESCRIPTION),
                    LocalDate.parse(doc.get(FIELD_CLOSED_DATE)),
                    doc.get(FIELD_IMPORTANT)
            );

            foundTickets.add(new TicketSearchResult(ticket, scoreDoc.score));
        }
    }

    private void buildSearchQuery(String[] words,
                                  BooleanQuery.Builder queryBuilder,
                                  SearchSettings settings) {

        for (String word : words) {

            String normalized = word.toLowerCase();

            if (normalized.isBlank()) {
                continue;
            }

            boolean isAllowedShortWord = allowedShortWordsProvider
                    .getAllowedShortWords()
                    .contains(normalized);

            // 🔹 short words
            if (isAllowedShortWord) {
                queryBuilder.add(
                        new TermQuery(new Term(FIELD_TITLE, normalized)),
                        BooleanClause.Occur.SHOULD
                );

                queryBuilder.add(
                        new TermQuery(new Term(FIELD_DESCRIPTION, normalized)),
                        BooleanClause.Occur.SHOULD
                );

                continue;
            }

            // 🔹 Regular words
            if (normalized.length() >= settings.minWordLength()) {

                // TITLE
                queryBuilder.add(
                        new BoostQuery(
                                new PrefixQuery(new Term(FIELD_TITLE, normalized)),
                                2.0f
                        ),
                        BooleanClause.Occur.SHOULD
                );

                queryBuilder.add(
                        new BoostQuery(
                                new FuzzyQuery(new Term(FIELD_TITLE, normalized), settings.maxEdits()),
                                1.5f
                        ),
                        BooleanClause.Occur.SHOULD
                );

                // DESCRIPTION
                queryBuilder.add(
                        new PrefixQuery(new Term(FIELD_DESCRIPTION, normalized)),
                        BooleanClause.Occur.SHOULD
                );

                queryBuilder.add(
                        new FuzzyQuery(new Term(FIELD_DESCRIPTION, normalized), settings.maxEdits()),
                        BooleanClause.Occur.SHOULD
                );

                // IMPORTANT WORDS (strongest)
                queryBuilder.add(
                        new BoostQuery(
                                new PrefixQuery(new Term(FIELD_IMPORTANT, normalized)),
                                3.0f
                        ),
                        BooleanClause.Occur.SHOULD
                );

                queryBuilder.add(
                        new BoostQuery(
                                new FuzzyQuery(new Term(FIELD_IMPORTANT, normalized), settings.maxEdits()),
                                3.0f
                        ),
                        BooleanClause.Occur.SHOULD
                );
            }
        }
    }

    private void createIndex(ByteBuffersDirectory index, IndexWriterConfig config) throws IOException {
        try (IndexWriter writer = new IndexWriter(index, config)) {
            for (Ticket ticket : ticketRepository.findAll()) {
                Document doc = new Document();

                doc.add(new StringField(FIELD_NUMBER, ticket.number(), Field.Store.YES));
                doc.add(new TextField(FIELD_TITLE, ticket.title(), Field.Store.YES));
                doc.add(new TextField(FIELD_DESCRIPTION, ticket.description(), Field.Store.YES));
                doc.add(new TextField(
                        FIELD_IMPORTANT,
                        ticket.importantWords() != null ? ticket.importantWords() : "",
                        Field.Store.YES
                ));
                doc.add(new StringField(FIELD_CLOSED_DATE, ticket.closedDate().toString(), Field.Store.YES));

                writer.addDocument(doc);
            }
        }
    }
}