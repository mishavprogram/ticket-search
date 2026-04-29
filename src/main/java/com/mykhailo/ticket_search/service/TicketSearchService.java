package com.mykhailo.ticket_search.service;

import com.mykhailo.ticket_search.model.Ticket;
import com.mykhailo.ticket_search.model.TicketSearchResult;
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
import org.apache.lucene.search.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketSearchService {

    private static final int MAX_RESULTS = 5;
    private static final int MAX_EDITS = 2;
    private static final int MIN_WORD_LENGTH = 3;
    private static final float MIN_SCORE = 1.0f;

    private static final String FIELD_NUMBER = "number";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_CLOSED_DATE = "closedDate";

    private final TicketRepository ticketRepository;

    public TicketSearchService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<TicketSearchResult> search(String text) throws Exception {
        Analyzer analyzer = new StandardAnalyzer();
        ByteBuffersDirectory index = new ByteBuffersDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        createIndex(index, config);

        return findTickets(text, index);
    }

    private static List<TicketSearchResult> findTickets(String text, ByteBuffersDirectory index) throws IOException {
        try (DirectoryReader reader = DirectoryReader.open(index)) {
            IndexSearcher searcher = new IndexSearcher(reader);

            BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

            String[] words = text.toLowerCase().split("\\s+");

            buildSearchQuery(words, queryBuilder);

            Query query = queryBuilder.build();

            TopDocs results = searcher.search(query, MAX_RESULTS);

            List<TicketSearchResult> foundTickets = new ArrayList<>();

            collectFoundTickets(results, searcher, foundTickets);

            return foundTickets;
        }
    }

    private static void collectFoundTickets(
            TopDocs results,
            IndexSearcher searcher,
            List<TicketSearchResult> foundTickets
    ) throws IOException {
        for (ScoreDoc scoreDoc : results.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);

            Ticket ticket = new Ticket(
                    doc.get(FIELD_NUMBER),
                    doc.get(FIELD_TITLE),
                    doc.get(FIELD_DESCRIPTION),
                    LocalDate.parse(doc.get(FIELD_CLOSED_DATE))
            );

            if (scoreDoc.score >= MIN_SCORE) {
                foundTickets.add(new TicketSearchResult(ticket, scoreDoc.score));
            }
        }
    }

    private static void buildSearchQuery(String[] words, BooleanQuery.Builder queryBuilder) {
        for (String word : words) {
            if (!word.isBlank() && word.length() >= MIN_WORD_LENGTH) {
                queryBuilder.add(
                        new PrefixQuery(new Term(FIELD_TITLE, word)),
                        BooleanClause.Occur.SHOULD
                );

                queryBuilder.add(
                        new PrefixQuery(new Term(FIELD_DESCRIPTION, word)),
                        BooleanClause.Occur.SHOULD
                );
                queryBuilder.add(
                        new FuzzyQuery(new Term(FIELD_TITLE, word), MAX_EDITS),
                        BooleanClause.Occur.SHOULD
                );

                queryBuilder.add(
                        new FuzzyQuery(new Term(FIELD_DESCRIPTION, word), MAX_EDITS),
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
                doc.add(new TextField(FIELD_CLOSED_DATE, ticket.closedDate().toString(), Field.Store.YES));

                writer.addDocument(doc);
            }
        }
    }
}