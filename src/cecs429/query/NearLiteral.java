package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.TokenProcessor;
import cecs429.util.MergeOperations;

import java.util.Arrays;
import java.util.List;

public class NearLiteral implements QueryComponent {
    private String originalWord;

    public NearLiteral(String term) {
        this.originalWord = term;

    }


    @Override
    public List<Posting> getPostings(Index index, TokenProcessor tokenProcessor) {
        List<String> parts = Arrays.asList(originalWord.split("\\s+"));

        String near = parts.get(1);
        //splitting "near/#" string to get integer value
        String[] nearParts = near.split("\\/");
        int integerK = Integer.valueOf(nearParts[1]);
        String first = tokenProcessor.processQueryToken(parts.get(0));
        String second = tokenProcessor.processQueryToken(parts.get(2));

        return MergeOperations.postionalIntersect(index.getPostings(first), index.getPostings(second), integerK);

    }
}
