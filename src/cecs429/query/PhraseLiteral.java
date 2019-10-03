package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.TokenProcessor;
import cecs429.util.MergeOperations;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a phrase literal consisting of one or more terms that must occur in sequence.
 */
public class PhraseLiteral implements QueryComponent {
    // The list of individual terms in the phrase.
    private List<String> mTerms = new ArrayList<>();
    public final int K_VALUE = 1;

    /**
     * Constructs a PhraseLiteral with the given individual phrase terms.
     */
    public PhraseLiteral(List<String> terms) {
        mTerms.addAll(terms);
    }

    /**
     * Constructs a PhraseLiteral given a string with one or more individual terms separated by spaces.
     */
    public PhraseLiteral(String terms) {
        mTerms.addAll(Arrays.asList(terms.split(" ")));
    }

    @Override
    public List<Posting> getPostings(Index index, TokenProcessor processor) {
        // normalize tokens
        List<String> normalizedTokens = new ArrayList<>(mTerms.stream().map(t -> processor.processQueryToken(t)).collect(Collectors.toList()));
        Iterator<String> terms = normalizedTokens.iterator();

        List<Posting> result = index.getPostings(terms.next()); // 0, 1, 3

        while (terms.hasNext()) {
            result = MergeOperations.postionalIntersect(result, index.getPostings(terms.next()), K_VALUE);
        }

        return result;
    }

    @Override
    public String toString() {
        return "\"" + String.join(" ", mTerms) + "\"";
    }
}
