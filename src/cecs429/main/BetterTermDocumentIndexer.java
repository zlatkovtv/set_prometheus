package cecs429.main;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.Index;
import cecs429.index.KGramIndex;
import cecs429.index.PositionalInvertedIndex;
import cecs429.index.Posting;
import cecs429.query.BooleanQueryParser;
import cecs429.query.QueryComponent;
import cecs429.text.AdvancedTokenProcessor;
import cecs429.text.EnglishTokenStream;
import cecs429.text.Stemmer;
import cecs429.text.TokenStream;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BetterTermDocumentIndexer {
    private DocumentCorpus corpus;
    private Index index;
    private static KGramIndex kGramIndex;
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
        long indexRunTime = (endTime - startTime) / 1000;

        return indexRunTime;
    }

    public DocumentCorpus getCorpus() {
        return this.corpus;
    }

    public List<Integer> runQuery(String query) {
        if (index == null) {
            throw new RuntimeException("Index has not been built yet.");
        }

        QueryComponent qc = queryParser.parseQuery(query);
        List<Integer> docIds = qc.getPostings(this.index, tokenProcessor)
                .stream()
                .map(x -> x.getDocumentId())
                .collect(Collectors.toList());

        Set<Integer> set = new HashSet<>(docIds);
        docIds.clear();
        docIds.addAll(set);
        Collections.sort(docIds);
        return docIds;
    }

    public String stemToken(String token) {
        return Stemmer.getInstance().stemToken(token);
    }

    public List<String> getVocabulary() {
        return this.index.getVocabulary();
    }

    public static KGramIndex getKGramIndex() {
        return kGramIndex;
    }

    public Index getIndex() {
        return index;
    }

    private DocumentCorpus loadCorpus(Path path) {
        return DirectoryCorpus.loadTextDirectory(path,".txt");
    }


    private Index indexCorpus(DocumentCorpus corpus) {
        if (corpus == null) {
            throw new RuntimeException("Corpus does not exist");
        }

        Index index = new PositionalInvertedIndex();
        kGramIndex = new KGramIndex();
        for (Document d : corpus.getDocuments()) {
            TokenStream ts = new EnglishTokenStream(d.getContent());
            int position = 0;
            for (String token : ts.getTokens()) {
                List<String> terms = this.tokenProcessor.processToken(token);
                for (String term : terms) {
                    if (term.isEmpty()) {
                        continue;
                    }

                    ((PositionalInvertedIndex) index).addTerm(term, d.getId(), position);
                    (kGramIndex).addTerm(term, d.getId());
                }

                position++;
            }
        }

        return index;
    }
}
