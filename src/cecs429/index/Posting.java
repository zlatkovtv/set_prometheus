package cecs429.index;

import java.util.ArrayList;
import java.util.List;

/**
 * A Posting encapulates a document ID associated with a search query component.
 */
public class Posting {
	private int mDocumentId;
	private List<Integer> mPositions;

	public Posting(int documentId) {
		mDocumentId = documentId;
		this.mPositions = new ArrayList<>();
	}

	public Posting(int documentId, List<Integer> positions) {
		mDocumentId = documentId;
		this.mPositions = positions;
	}

	public void addPosition(int position) {
		this.mPositions.add(position);
	}
	
	public int getDocumentId() {
		return mDocumentId;
	}

	public List<Integer> getPositions() {
		return this.mPositions;
	}

	@Override
	public boolean equals(Object obj) {
		Posting second = (Posting) obj;
		return this.getDocumentId() == second.getDocumentId();
	}
}
