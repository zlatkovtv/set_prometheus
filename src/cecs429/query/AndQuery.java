package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;

import java.util.ArrayList;
import java.util.Iterator;
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
	
	@Override
	public List<Posting> getPostings(Index index) {
		Iterator<QueryComponent> iterator = mComponents.iterator();

		List<Posting> result = new ArrayList<>();

		result.addAll(iterator.next().getPostings(index));

		for (; iterator.hasNext(); ) {
			QueryComponent component = iterator.next();
			result.retainAll(component.getPostings(index));
		}

		// we retain all elements that are already there. we dont add postings with duplicate ids. change?
		return result;
	}
	
	@Override
	public String toString() {
		return
		 String.join(" ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()));
	}
}
