package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.TokenProcessor;
import cecs429.util.MergeOperations;

import javax.management.Query;
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
        String first = parts.get(0);
        String second = parts.get(2);

        QueryComponent firstComponent = getType(first);
        QueryComponent secondComponent = getType(second);

        return MergeOperations.postionalIntersect(firstComponent.getPostings(index, tokenProcessor), secondComponent.getPostings(index,tokenProcessor), integerK);

    }

    private QueryComponent getType(String string) {
        if(string.charAt(0) == '\"' && string.charAt(string.length()-1) == '\"') {
            //phrase literal
            return new PhraseLiteral(string.substring(1, string.length()-2));
        } else if(string.contains("*")) {
            //wildcard literal
            return new WildcardLiteral(string);
        } else {
            return new TermLiteral(string);
        }
    }
}
