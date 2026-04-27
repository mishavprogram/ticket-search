package com.mykhailo.ticket_search.service;

import com.mykhailo.ticket_search.model.Ticket;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketSearchService {

    private final TicketRepository ticketRepository;
    private static final int MAX_EDITS = 2;

    public TicketSearchService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> search(String text) throws Exception {
        Analyzer analyzer = new StandardAnalyzer();
        ByteBuffersDirectory index = new ByteBuffersDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        createIndex(index, config);

        return findTickets(text, index);
    }

    private static List<Ticket> findTickets(String text, ByteBuffersDirectory index) throws IOException {
        try (DirectoryReader reader = DirectoryReader.open(index)) {
            IndexSearcher searcher = new IndexSearcher(reader);

            BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

            String[] words = text.toLowerCase().split("\\s+");

            buildSearchQuery(words, queryBuilder);

            Query query = queryBuilder.build();

            TopDocs results = searcher.search(query, 2);

            List<Ticket> foundTickets = new ArrayList<>();

            collectFoundTickets(results, searcher, foundTickets);

            return foundTickets;
        }
    }

    private static void collectFoundTickets(TopDocs results, IndexSearcher searcher, List<Ticket> foundTickets) throws IOException {
        for (ScoreDoc scoreDoc : results.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);

            foundTickets.add(new Ticket(
                    doc.get("number"),
                    doc.get("title"),
                    doc.get("description")
            ));
        }
    }

    private static void buildSearchQuery(String[] words, BooleanQuery.Builder queryBuilder) {
        for (String word : words) {
            if (!word.isBlank()) {
                queryBuilder.add(
                        new PrefixQuery(new Term("title", word)),
                        BooleanClause.Occur.SHOULD
                );

                queryBuilder.add(
                        new PrefixQuery(new Term("description", word)),
                        BooleanClause.Occur.SHOULD
                );
                queryBuilder.add(
                        new FuzzyQuery(new Term("title", word), MAX_EDITS),
                        BooleanClause.Occur.SHOULD
                );

                queryBuilder.add(
                        new FuzzyQuery(new Term("description", word), MAX_EDITS),
                        BooleanClause.Occur.SHOULD
                );
            }
        }
    }

    private void createIndex(ByteBuffersDirectory index, IndexWriterConfig config) throws IOException {
        try (IndexWriter writer = new IndexWriter(index, config)) {
            for (Ticket ticket : ticketRepository.findAll()) {
                Document doc = new Document();

                doc.add(new StringField("number", ticket.number(), Field.Store.YES));
                doc.add(new TextField("title", ticket.title(), Field.Store.YES));
                doc.add(new TextField("description", ticket.description(), Field.Store.YES));

                writer.addDocument(doc);
            }
        }
    }
}