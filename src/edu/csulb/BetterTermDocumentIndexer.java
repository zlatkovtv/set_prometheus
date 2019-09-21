package edu.csulb;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.Index;
import cecs429.index.InvertedIndex;
import cecs429.index.PositionalInvertedIndex;
import cecs429.index.Posting;
import cecs429.query.BooleanQueryParser;
import cecs429.query.QueryComponent;
import cecs429.text.AdvancedTokenProcessor;
//import cecs429.text.BasicTokenProcessor;
import cecs429.text.EnglishTokenStream;
import cecs429.text.TokenStream;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class BetterTermDocumentIndexer {

    static Scanner reader = new Scanner(System.in);

    public static void main(String[] args) {
//        When we do UI, include option to choose directory type
        DocumentCorpus corpus = DirectoryCorpus.loadTextDirectory(Paths.get("moby-dick").toAbsolutePath(), ".txt");
        Index index = indexCorpus(corpus);
        String query;
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
                    for (Posting p : qc.getPostings(index)) {
                        System.out.println("Document ID " + p.getDocumentId());
                    }
                    break;
            }
        }

    }

    private static Index indexCorpus(DocumentCorpus corpus) {
        HashSet<String> vocabulary = new HashSet<>();
        AdvancedTokenProcessor processor = new AdvancedTokenProcessor();

        // First, build the vocabulary hash set.
        for (Document d : corpus.getDocuments()) {
            TokenStream ts = new EnglishTokenStream(d.getContent());
            for (String token : ts.getTokens()) {
                //add all return a list 
                vocabulary.addAll(processor.processToken(token));
            }
        }

        Index index = new PositionalInvertedIndex();
        for (Document d : corpus.getDocuments()) {
            TokenStream ts = new EnglishTokenStream(d.getContent());
            int position = 0;
            for (String token : ts.getTokens()) {
                List<String> terms = processor.processToken(token);
                //To D0: Ask Neil how to posiiton mutli-word terms.
                for (String term: terms ) {
                    ((PositionalInvertedIndex) index).addTerm(term, d.getId(), position);
                }

                position++;
            }
        }

        return index;
    }
}
