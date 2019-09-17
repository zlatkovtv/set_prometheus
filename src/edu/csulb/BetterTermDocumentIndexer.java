package edu.csulb;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.Index;
import cecs429.index.InvertedIndex;
import cecs429.index.Posting;
import cecs429.text.BasicTokenProcessor;
import cecs429.text.EnglishTokenStream;
import cecs429.text.TokenStream;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;

public class BetterTermDocumentIndexer {

    static Scanner reader = new Scanner(System.in);

    public static void main(String[] args) {
//		DocumentCorpus corpus = DirectoryCorpus.loadTextDirectory(Paths.get("").toAbsolutePath(), ".txt");
//		Index index = indexCorpus(corpus) ;
//		// We aren't ready to use a full query parser; for now, we'll only support single-term queries.
//		String query = "whale"; // hard-coded search for "whale"
//		for (Posting p : index.getPostings(query)) {
//			System.out.println("Document " + corpus.getDocument(p.getDocumentId()).getTitle());
//		}

        DocumentCorpus corpus = DirectoryCorpus.loadTextDirectory(Paths.get("").toAbsolutePath(), ".txt");
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
                    for (Posting p : index.getPostings(input)) {
                        System.out.println("Document ID " + p.getDocumentId());
                    }
                    break;
            }
        }

    }

    private static Index indexCorpus(DocumentCorpus corpus) {
        HashSet<String> vocabulary = new HashSet<>();
        BasicTokenProcessor processor = new BasicTokenProcessor();

        // First, build the vocabulary hash set.
        for (Document d : corpus.getDocuments()) {
            TokenStream ts = new EnglishTokenStream(d.getContent());
            for (String token : ts.getTokens()) {
                //add all return a list 
                vocabulary.add(processor.processToken(token));
            }
        }

        Index index = new InvertedIndex();
        for (Document d : corpus.getDocuments()) {
            TokenStream ts = new EnglishTokenStream(d.getContent());
            for (String token : ts.getTokens()) {
                ((InvertedIndex) index).addTerm(processor.processToken(token), d.getId());
            }
        }
        // TODO:
        // Get all the documents in the corpus by calling GetDocuments().
        // Iterate through the documents, and:
        // Tokenize the document's content by constructing an EnglishTokenStream around the document's content.
        // Iterate through the tokens in the document, processing them using a BasicTokenProcessor,
        //		and adding them to the HashSet vocabulary.

        // TODO:
        // Constuct a TermDocumentMatrix once you know the size of the vocabulary.
        // THEN, do the loop again! But instead of inserting into the HashSet, add terms to the index with addPosting.
        return index;
    }
}
