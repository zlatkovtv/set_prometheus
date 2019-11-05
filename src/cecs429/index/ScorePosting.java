package cecs429.index;

public class ScorePosting extends Posting{


    private double accumulator = 0;

    public ScorePosting(int documentId, double score) {
        super(documentId);
        this.accumulator = score;
    }

    public double getAccumulator() {
        return accumulator;
    }
}
