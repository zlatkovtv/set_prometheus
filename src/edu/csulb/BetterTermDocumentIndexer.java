package edu.csulb;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.Index;
import cecs429.index.PositionalInvertedIndex;
import cecs429.index.Posting;
import cecs429.query.BooleanQueryParser;
import cecs429.query.QueryComponent;
import cecs429.text.AdvancedTokenProcessor;
import cecs429.text.EnglishTokenStream;
import cecs429.text.Stemmer;
import cecs429.text.TokenStream;
import cecs429.ui.MainFrameController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class BetterTermDocumentIndexer {
    private static Scanner reader = new Scanner(System.in);
    private Index index;

    public void run(Path path) {
        DocumentCorpus corpus = loadCorpus(path);

        long startTime = System.currentTimeMillis();
        index = indexCorpus(corpus);
        long endTime = System.currentTimeMillis();

        System.out.println("That took " + (endTime - startTime)/1000.0 + " Seconds");

        String input;
        while (true) {
            System.out.printf("Enter search term or quit to exit. ");
            input = reader.nextLine().toLowerCase();
            switch (input) {
                case "quit":
                    System.exit(0);
                    break;

                default:
                    BooleanQueryParser bqp = new BooleanQueryParser();
                    QueryComponent qc = bqp.parseQuery(input);
                    AdvancedTokenProcessor pr = new AdvancedTokenProcessor();
                    for (Posting p : qc.getPostings(this.index, pr)) {
                        System.out.println("Document ID " + p.getDocumentId());
                    }
                    break;
            }
        }
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

    private String stemToken(String token) {
        return Stemmer.getInstance().stemToken(token);
    }

    private List<String> getTermsFromVocabulary(int quantity) {
        return this.index.getVocabulary()
                .stream()
                .limit(quantity)
                .collect(Collectors.toList());
    }
}
