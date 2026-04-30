package com.mykhailo.ticket_search.service;

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
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
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

    private final TicketRepository ticketRepository;
    private final SearchSettings searchSettings = SearchSettings.defaultSettings();

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

    private List<TicketSearchResult> findTickets(String text, ByteBuffersDirectory index) throws IOException {
        try (DirectoryReader reader = DirectoryReader.open(index)) {
            IndexSearcher searcher = new IndexSearcher(reader);

            BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

            String[] words = text.toLowerCase().split("\\s+");

            buildSearchQuery(words, queryBuilder);

            Query query = queryBuilder.build();

            TopDocs results = searcher.search(query, searchSettings.maxResults());

            List<TicketSearchResult> foundTickets = new ArrayList<>();

            collectFoundTickets(results, searcher, foundTickets);

            return foundTickets;
        }
    }

    private void collectFoundTickets(
            TopDocs results,
            IndexSearcher searcher,
            List<TicketSearchResult> foundTickets
    ) throws IOException {
        if (results.scoreDocs.length == 0) {
            return;
        }

        float topScore = results.scoreDocs[0].score;
        float minAllowedScore = topScore * searchSettings.minScoreRatio();

        for (ScoreDoc scoreDoc : results.scoreDocs) {
            if (scoreDoc.score < minAllowedScore) {
                continue;
            }

            Document doc = searcher.doc(scoreDoc.doc);

            Ticket ticket = new Ticket(
                    doc.get(FIELD_NUMBER),
                    doc.get(FIELD_TITLE),
                    doc.get(FIELD_DESCRIPTION),
                    LocalDate.parse(doc.get(FIELD_CLOSED_DATE))
            );

            foundTickets.add(new TicketSearchResult(ticket, scoreDoc.score));
        }
    }

    private void buildSearchQuery(String[] words, BooleanQuery.Builder queryBuilder) {
        for (String word : words) {
            if (!word.isBlank() && word.length() >= searchSettings.minWordLength()) {
                queryBuilder.add(
                        new PrefixQuery(new Term(FIELD_TITLE, word)),
                        BooleanClause.Occur.SHOULD
                );

                queryBuilder.add(
                        new PrefixQuery(new Term(FIELD_DESCRIPTION, word)),
                        BooleanClause.Occur.SHOULD
                );
                queryBuilder.add(
                        new FuzzyQuery(new Term(FIELD_TITLE, word), searchSettings.maxEdits()),
                        BooleanClause.Occur.SHOULD
                );

                queryBuilder.add(
                        new FuzzyQuery(new Term(FIELD_DESCRIPTION, word), searchSettings.maxEdits()),
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
                doc.add(new StringField(FIELD_CLOSED_DATE, ticket.closedDate().toString(), Field.Store.YES));

                writer.addDocument(doc);
            }
        }
    }
}