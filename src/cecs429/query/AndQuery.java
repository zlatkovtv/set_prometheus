package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.TokenProcessor;
import cecs429.util.MergeOperations;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An AndQuery composes other QueryComponents and merges their postings in an intersection-like operation.
 */
public class AndQuery implements QueryComponent {
    private List<QueryComponent> mComponents;

    public AndQuery(List<QueryComponent> components) {
        mComponents = components;
    }

//    @Override
//    public List<Posting> getPostings(Index index, TokenProcessor processor) {
//
//        List<Posting> result = new ArrayList<>();
//
//        result.addAll(mComponents.get(0).getPostings(index, processor));
//        int checker =0;
//        for (int i = 1; i < mComponents.size(); i++) {
//            if (checker == 0) {
//                if(mComponents.get(0) instanceof NotQuery || mComponents.get(i) instanceof NotQuery ) {
//                    result = MergeOperations.NotMerge(result, mComponents.get(i).getPostings(index, processor));
//                } else {
//                    result = MergeOperations.intersectMerge(result, mComponents.get(i).getPostings(index, processor));
//                }
//                checker = 1;
//            }
//            if( mComponents.get(i) instanceof NotQuery ) {
//                result = MergeOperations.NotMerge(result, mComponents.get(i).getPostings(index, processor));
//            } else {
//                result = MergeOperations.intersectMerge(result, mComponents.get(i).getPostings(index, processor));
//            }
//
//        }
//
//        return result;
//    }

    @Override
    public List<Posting> getPostings(Index index, TokenProcessor processor) {

        List<Posting> result = new ArrayList<>();

        result.addAll(mComponents.get(0).getPostings(index, processor));
        boolean checker = mComponents.get(0) instanceof NotQuery;
        for (int i = 1; i < mComponents.size(); i++) {
            if(checker) {
                result = MergeOperations.notMerge(mComponents.get(i).getPostings(index, processor), result);
            }
            else if (mComponents.get(i) instanceof NotQuery)  {
                result = MergeOperations.notMerge(result, mComponents.get(i).getPostings(index, processor));
            }
            else {
                result = MergeOperations.intersectMerge(result, mComponents.get(i).getPostings(index, processor));
            }

            checker = false;
        }

        return result;
    }

    @Override
    public String toString() {
        return
                String.join(" ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()));
    }
}


