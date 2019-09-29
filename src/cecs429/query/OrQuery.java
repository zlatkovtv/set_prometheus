package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.TokenProcessor;
import cecs429.util.MergeOperations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An OrQuery composes other QueryComponents and merges their postings with a union-type operation.
 */
public class OrQuery implements QueryComponent {
	// The components of the Or query.
	private List<QueryComponent> mComponents;

	public OrQuery(List<QueryComponent> components) {
		mComponents = components;
	}

	@Override
	public List<Posting> getPostings(Index index, TokenProcessor processor) {
		List<Posting> result = new ArrayList<>();

		result.addAll(mComponents.get(0).getPostings(index, processor));

		for (int i = 1; i < mComponents.size(); i++) {
			result = MergeOperations.unionMerge(result, mComponents.get(i).getPostings(index, processor));
		}
		return result;
	}



	@Override
	public String toString() {
		// Returns a string of the form "[SUBQUERY] + [SUBQUERY] + [SUBQUERY]"
		return "(" +
				String.join(" + ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()))
				+ " )";
	}
}
