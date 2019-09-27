package edu.csulb;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.Index;
import cecs429.index.PositionalInvertedIndex;
import cecs429.query.BooleanQueryParser;
import cecs429.query.QueryComponent;
import cecs429.text.AdvancedTokenProcessor;
import cecs429.text.EnglishTokenStream;
import cecs429.text.Stemmer;
import cecs429.text.TokenStream;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class BetterTermDocumentIndexer {
    private DocumentCorpus corpus;
    private Index index;
    private BooleanQueryParser queryParser;
    private AdvancedTokenProcessor tokenProcessor;

    public BetterTermDocumentIndexer() {
        queryParser = new BooleanQueryParser();
        tokenProcessor = new AdvancedTokenProcessor();
    }

    public long runIndexer(Path path) {
        corpus = loadCorpus(path);
        long startTime = System.currentTimeMillis();
        index = indexCorpus(corpus);
        long endTime = System.currentTimeMillis();
        long indexRunTime = (endTime - startTime)/1000;

        return indexRunTime;
    }

    public DocumentCorpus getCorpus() {
        return this.corpus;
    }

    public List<Integer> runQuery(String query) {
        if(index == null) {
            throw new RuntimeException("Index has not been built yet.");
        }

        QueryComponent qc = queryParser.parseQuery(query);
        return qc.getPostings(this.index, tokenProcessor).stream().map(x -> x.getDocumentId()).collect(Collectors.toList());
    }

    public String stemToken(String token) {
        return Stemmer.getInstance().stemToken(token);
    }

    public List<String> getVocabulary() {
        return this.index.getVocabulary();
    }

    private DocumentCorpus loadCorpus(Path path) {
        return DirectoryCorpus.loadJsonDirectory(path);
    }

    private static Index indexCorpus(DocumentCorpus corpus) {
        AdvancedTokenProcessor processor = new AdvancedTokenProcessor();

        Index index = new PositionalInvertedIndex();
        for (Document d : corpus.getDocuments()) {
            TokenStream ts = new EnglishTokenStream(d.getContent());
            int position = 0;
            for (String token : ts.getTokens()) {
                List<String> terms = processor.processToken(token);
                for (String term: terms ) {
                    ((PositionalInvertedIndex) index).addTerm(term, d.getId(), position);
                }

                position++;
            }
        }

        return index;
    }
}
