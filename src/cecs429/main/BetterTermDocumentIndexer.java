package cecs429.main;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.*;
import cecs429.query.BooleanQueryParser;
import cecs429.query.QueryComponent;
import cecs429.query.RankedQuery;
import cecs429.text.AdvancedTokenProcessor;
import cecs429.text.EnglishTokenStream;
import cecs429.text.Stemmer;
import cecs429.text.TokenStream;

import java.nio.file.Path;
import java.util.*;

public class BetterTermDocumentIndexer {
    private DocumentCorpus corpus;
    private Index index;
    private Index diskIndex;
    private static KGramIndex kGramIndex;
    private BooleanQueryParser queryParser;
    private AdvancedTokenProcessor tokenProcessor;
    private ArrayList<Double> ld;

    public BetterTermDocumentIndexer(Path path) {
        queryParser = new BooleanQueryParser();
        tokenProcessor = new AdvancedTokenProcessor();
        corpus = loadCorpus(path);
    }

    public long runIndexer() {
        long startTime = System.currentTimeMillis();
        index = indexCorpus(corpus);
        long endTime = System.currentTimeMillis();
        long indexRunTime = (endTime - startTime) / 1000;

        return indexRunTime;
    }

    public DocumentCorpus getCorpus() {
        return this.corpus;
    }

    public List<Posting> runQuery(String query, boolean ranked) {
        if (this.diskIndex == null) {
            throw new RuntimeException("Index has not been built yet.");
        }

        QueryComponent qc;

        if(ranked) {
            qc = new RankedQuery(query, corpus.getCorpusSize());
        } else {
            qc  = queryParser.parseQuery(query);
        }

        return qc.getPostings(this.diskIndex, tokenProcessor);
    }

    public String stemToken(String token) {
        return Stemmer.getInstance().stemToken(token);
    }

    public List<String> getVocabulary() {
        return this.index.getVocabulary();
    }

    public Index getIndex() {
        return this.index;
    }

    public ArrayList<Double> getDocWeight() {
        return this.ld;
    }

    public static KGramIndex getKGramIndex() {
        return kGramIndex;
    }

    private DocumentCorpus loadCorpus(Path path) {
        return DirectoryCorpus.loadTextDirectory(path, ".txt");
    }

    private Index indexCorpus(DocumentCorpus corpus) {
        ld = new ArrayList<>();

        if (corpus == null) {
            throw new RuntimeException("Corpus does not exist");
        }

        Index index = new PositionalInvertedIndex();
        kGramIndex = new KGramIndex();
        for (Document d : corpus.getDocuments()) {
            HashMap <String,Double> documentWeight  = new HashMap <String, Double>();
            TokenStream ts = new EnglishTokenStream(d.getContent());
            int position = 0;
            for (String token : ts.getTokens()) {
                List<String> terms = this.tokenProcessor.processToken(token);

                for (String term : terms) {

                    if(term.isEmpty()) {
                        continue;
                    }
                    if(!documentWeight.containsKey(term)) {

                        documentWeight.put(term,1.0);
                    } else {
                        documentWeight.put(term, documentWeight.get(term) + 1.0);
                    }

                    ((PositionalInvertedIndex) index).addTerm(term, d.getId(), position);
                    (kGramIndex).addTerm(term, d.getId());
                }

                position++;
            }
            double sum =0;
            for (double tftd : documentWeight.values()) {
                double weight = calcWDT(tftd);
                sum += Math.pow(weight,2);

            }
            sum = Math.sqrt(sum);
            ld.add(sum);

        }

        return index;
    }

    private double calcWDT(double tftd) {
        return (1 + Math.log(tftd));
    }

    public List<Posting> getResults(String term, String path, boolean ranked) {
        this.diskIndex = new DiskPositionalIndex(path);
        return runQuery(term, ranked);
    }
}
