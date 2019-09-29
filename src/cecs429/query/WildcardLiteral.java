package cecs429.query;

import cecs429.index.Index;
import cecs429.index.KGramIndex;
import cecs429.index.Posting;
import cecs429.main.BetterTermDocumentIndexer;
import cecs429.text.TokenProcessor;
import cecs429.util.KGramIterator;
import cecs429.util.MergeOperations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class WildcardLiteral implements QueryComponent {
    private final int MIN_GRAM = 1;
    private final int MAX_GRAM = 3;
    private String originalWord;
    private List<String> kGrams = new ArrayList<>();

    public WildcardLiteral(String term) {
        this.originalWord = term;

    }
//
//    private void findKgram() {
//        for(int i = 0; i < parts.size(); i++) {
//            for (int j = (i + MIN_GRAM); i < Math.min(parts.size(), (i + MAX_GRAM) + 1); j++) {
//                System.out.println(i);
//                System.out.println(j);
//            }
//        }
//    }

    @Override
    public List<Posting> getPostings(Index index, TokenProcessor tokenProcessor) {
        originalWord = tokenProcessor.processQueryToken(originalWord);
        StringBuilder sb = new StringBuilder(originalWord);
        sb.insert(0, '$');
        sb.append('$');
        String term = sb.toString();
        List<String> parts = Arrays.asList(term.split("\\*"));
        int kgramSize = MAX_GRAM;
        for (String part: parts) {
            if((part.length() < kgramSize) && part.length() <= 3) {
                kgramSize = part.length();
            }
        }
        Iterator iterator;
        for (String part: parts) {
            iterator = new KGramIterator(kgramSize, part);
            while(iterator.hasNext()) {
                String nextGram = iterator.next().toString();
                if(!kGrams.contains(nextGram)) {
                    kGrams.add(nextGram);
                }
            }
        }

        // TODO Star gives extra results, + gives same as neils'. Figure out which one
        String regexPattern = originalWord.replace("*", ".*");
        KGramIndex kGramIndex = BetterTermDocumentIndexer.getKGramIndex();
        List<String> candidates = new ArrayList<>();

        for (String gram: kGrams) {
            for (String candidate: kGramIndex.getCandidates(gram)) {
                String normalizedCandidate = tokenProcessor.normalizeToken(candidate);
                if(candidate.matches(regexPattern) && (!candidates.contains(normalizedCandidate))) {
                    candidates.add(normalizedCandidate);
                }
            }
        }

        List<Posting> results = index.getPostings(candidates.get(0));

        for (int i = 1; i < candidates.size(); i++) {
            results = MergeOperations.unionMerge(results, index.getPostings(candidates.get(i)));
        }

        return results;
    }
}
