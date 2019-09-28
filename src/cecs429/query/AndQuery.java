package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.TokenProcessor;

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
	public List<Posting> getPostings(Index index, TokenProcessor processor) {
		//Iterator<QueryComponent> iterator = mComponents.iterator();

		List<Posting> result = new ArrayList<>();

		result.addAll(mComponents.get(0).getPostings(index, processor));

		for (int i = 1; i < mComponents.size(); i++) {
			result = intersectMerge(result, mComponents.get(i).getPostings(index, processor));
		}

//        for (; iterator.hasNext(); ) {
//            QueryComponent component = iterator.next();
//            result.retainAll(component.getPostings(index, processor));
//        }

		// we retain all elements that are already there. we dont add postings with duplicate ids. change?
		return result;
	}

	private List<Posting> intersectMerge(List<Posting> pList1, List<Posting> pList2) {
		// Reference https://www.geeksforgeeks.org/two-pointers-technique/
		//This is written similar to how Dr. Terrel wrote on the board. I tried to mimic it as much
		//Declare "Pointers"
		int itr = 0;
		int jtr = 0;
		List<Posting> tmp = new ArrayList<>(); //Temporary list to hold resultes

		int pList1Size = pList1.size();
		int pList2Size = pList2.size();

		while (itr < pList1Size && jtr < pList2Size) {
			if (pList1.get(itr).getDocumentId() == pList2.get(jtr).getDocumentId()) {
				tmp.add(pList1.get(itr));
				++itr;
				++jtr;
			} else if (pList1.get(itr).getDocumentId() < pList2.get(jtr).getDocumentId()) {
				++itr;
			} else if (pList2.get(jtr).getDocumentId() < pList1.get(itr).getDocumentId()) {
				++jtr;
			}
		}

		return tmp;

	}

	@Override
	public String toString() {
		return
				String.join(" ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()));
	}
}


