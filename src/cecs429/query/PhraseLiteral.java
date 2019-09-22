package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;

import java.util.*;

/**
 * Represents a phrase literal consisting of one or more terms that must occur in sequence.
 */
public class PhraseLiteral implements QueryComponent {
	// The list of individual terms in the phrase.
	private List<String> mTerms = new ArrayList<>();
	
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
	public List<Posting> getPostings(Index index) {
		// couch bag tree
		List<Posting> firstTermPostings = index.getPostings(mTerms.get(0)); // 0, 1, 3
		Map<Integer, List<List<Integer>>> results = new TreeMap<>();

		for (int i = 0; i < firstTermPostings.size(); i++) {
			Posting p = firstTermPostings.get(i);
			List<List<Integer>> tmpList = new ArrayList<>();
			tmpList.add(p.getPositions());
			results.put(p.getDocumentId(), tmpList);
		}

		int k = 1;
		for(Map.Entry<Integer, List<List<Integer>>> entry : results.entrySet()) {
			for (int j = k; j < mTerms.size(); j++) {
				List<Posting> nextPostingList = index.getPostings(mTerms.get(j)); // 0, 2, 3
				if(!nextPostingList.stream().filter(o -> o.getDocumentId() == (entry.getKey())).findFirst().isPresent()) {
					// test below
					results.remove(entry.getKey());
					continue;
				}

				List<List<Integer>> positionsList = results.get(entry.getKey());
				positionsList.add(nextPostingList.get(j).getPositions());
				results.put(entry.getKey(), positionsList);
			}
			k++;
		}

		return null;
		// TODO: program this method. Retrieve the postings for the individual terms in the phrase,
		// and positional merge them together.
	}
	
	@Override
	public String toString() {
		return "\"" + String.join(" ", mTerms) + "\"";
	}
}
