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
		Iterator<String> terms = mTerms.iterator();
		//no terms detected
		if(!terms.hasNext()) {
			return new ArrayList<>();
		}

		List<Posting> firstTermPostings = index.getPostings(terms.next()); // 0, 1, 3
		Map<Integer, List<Integer>> results = new TreeMap<>();

		for (int i = 0; i < firstTermPostings.size(); i++) {
			Posting p = firstTermPostings.get(i);
			results.put(p.getDocumentId(), p.getPositions());
		}

		Iterator<Map.Entry<Integer, List<Integer>>> mapIterator = new TreeMap<>(results).entrySet().iterator();
		while(mapIterator.hasNext()) {
			Map.Entry<Integer, List<Integer>> entry = mapIterator.next();
			Integer currentDocumentId = entry.getKey();
			List<Integer> currentPositions = entry.getValue();

			//restart iterator and start at index 1
			terms = mTerms.iterator();
			terms.next();

			while (terms.hasNext()) {
				List<Posting> nextPostingList = index.getPostings(terms.next());
				Posting posting = nextPostingList.stream()
						.filter(p -> p.getDocumentId() == currentDocumentId)
						.findFirst()
						.orElse(null);

				if(posting == null) {
					results.remove(currentDocumentId);
					break;
				}

				List<Integer> mergedPositions = new ArrayList<>();

				for (int i = 0; i < currentPositions.size(); i++){
					for (Integer nextPostingPosition: posting.getPositions()) {
						if(((currentPositions.get(i) + 1) == nextPostingPosition)) {
							mergedPositions.add(currentPositions.get(i));
							mergedPositions.add(currentPositions.get(i) + 1);
						}
					}
				}

				// no adjacent positions for term
				if(mergedPositions.size() == 0) {
					results.remove(currentDocumentId);
					break;
				}

				results.put(currentDocumentId, mergedPositions);
			}
		}

		return buildIntoPostinsList(results);
	}

	private List<Posting> buildIntoPostinsList(Map<Integer, List<Integer>> results) {
		List<Posting> postings = new ArrayList<>();
		for (Map.Entry<Integer, List<Integer>> entry: results.entrySet()) {
			List<Integer> positions = entry.getValue();
			Collections.sort(positions);
			postings.add(new Posting(entry.getKey(), positions));
		}

		return postings;
	}

	@Override
	public String toString() {
		return "\"" + String.join(" ", mTerms) + "\"";
	}
}
