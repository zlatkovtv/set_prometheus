package cecs429.index;

import java.util.List;

public class ScorePosting extends Posting{


    private double accumilator = 0;

    public ScorePosting(int documentId, double score) {
        super(documentId);
        this.accumilator = score;
    }

    public ScorePosting(int documentId) {
        super(documentId);
    }

    public ScorePosting(int documentId, List<Integer> positions) {
        super(documentId, positions);
    }
    public double getAccumilator() {
        return accumilator;
    }
}
