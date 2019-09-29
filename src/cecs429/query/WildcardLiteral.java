package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.main.BetterTermDocumentIndexer;
import cecs429.text.TokenProcessor;
import cecs429.util.KGramIterator;
import cecs429.util.MergeOperations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WildcardLiteral implements QueryComponent {
    private final int MIN_GRAM = 1;
    private final int MAX_GRAM = 3;
    private List<String> kGrams = new ArrayList<>();

    public WildcardLiteral(String term) {
        StringBuilder sb = new StringBuilder(term);
        sb.insert(0, '$');
        sb.append('$');
        term = sb.toString();
        List<String> parts = Arrays.asList(term.split("\\*"));
        int kgramSize = MAX_GRAM;
        for (String part: parts) {
            if((part.length() < kgramSize) && part.length() <= 3) {
                kgramSize = part.length();
            }
        }
        Iterator i = null;
        for (String part: parts) {
            i = new KGramIterator(kgramSize, part);
            while(i.hasNext()) {
                String nextGram = i.next().toString();
                if(!kGrams.contains(nextGram)) {
                    kGrams.add(nextGram);
                }
            }
        }
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
        Index kGramIndex = BetterTermDocumentIndexer.getKGramIndex();
        List<Posting> result = new ArrayList<>();

        result.addAll(kGramIndex.getPostings(kGrams.get(0)));

        for (int i = 1; i < kGrams.size(); i++) {
            result = MergeOperations.unionMerge(result, kGramIndex.getPostings(kGrams.get(i)));
        }

        return result;
    }
}
