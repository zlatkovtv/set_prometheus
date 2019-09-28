package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.TokenProcessor;

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
			result = unionMerge(result, mComponents.get(i).getPostings(index, processor));
		}
		return result;
	}

	private List<Posting> unionMerge(List<Posting> pList1, List<Posting> pList2) {
		// Reference https://www.geeksforgeeks.org/two-pointers-technique/
		// FYI The logic here is the same as a leet code question
		int i = 0, j = 0;
		List<Posting> tmp = new ArrayList<>();

		int pList1Size = pList1.size();
		int pList2Size = pList2.size();

		//while both lists are inbounds
		while (i < pList1Size && j < pList2Size) {
			if (pList1.get(i).getDocumentId() == pList2.get(j).getDocumentId()) {
				tmp.add(pList1.get(i));
				++i;
				++j;
			} else if (pList1.get(i).getDocumentId() < pList2.get(j).getDocumentId()) {
				tmp.add(pList1.get(i));
				++i;
			} else if (pList2.get(j).getDocumentId() < pList1.get(i).getDocumentId()) {
				tmp.add(pList2.get(j));
				++j;
			}
		}

		//Add the rest of either list if lists are not the same size
		if (pList1Size != pList2Size) {
			if (i == pList1.size()) {
				//add the rest of list2
				tmp.addAll(pList2.subList(j, pList2Size));
			} else if (j == pList2.size()) {
				//add the rest of list1
				tmp.addAll(pList1.subList(i, pList1Size));
			}
		}

		return tmp;
	}

	@Override
	public String toString() {
		// Returns a string of the form "[SUBQUERY] + [SUBQUERY] + [SUBQUERY]"
		return "(" +
				String.join(" + ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()))
				+ " )";
	}
}
