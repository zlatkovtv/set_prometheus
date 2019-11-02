package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.TokenProcessor;
import java.util.List;

public class NotQuery implements QueryComponent {
    private QueryComponent mComponents;

    public NotQuery(QueryComponent components) {
        mComponents = components;
    }

    @Override
    public List<Posting> getPostings(Index index, TokenProcessor processor) {

        return mComponents.getPostings(index, processor);
    }

    @Override
    public String toString() {
        return mComponents.toString();
    }
}
