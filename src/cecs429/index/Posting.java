package cecs429.index;

import java.util.ArrayList;
import java.util.List;

/**
 * A Posting encapulates a document ID associated with a search query component.
 */
public class Posting {
	private int mDocumentId;
	private List<Integer> mPositions;

	public int getmDocumentId() {
		return mDocumentId;
	}

	public void setmDocumentId(int mDocumentId) {
		this.mDocumentId = mDocumentId;
	}

	public List<Integer> getmPositions() {
		return mPositions;
	}

	public void setmPositions(List<Integer> mPositions) {
		this.mPositions = mPositions;
	}
//
//	public Posting(int documentId, List<Integer> mPositions) {
//		mDocumentId = documentId;
//		this.mPositions = mPositions;
//	}

	public Posting(int documentId) {
		mDocumentId = documentId;
		this.mPositions = new ArrayList<>();
	}

	public void addPosition(int position) {
		this.mPositions.add(position);
	}
	
	public int getDocumentId() {
		return mDocumentId;
	}
}
