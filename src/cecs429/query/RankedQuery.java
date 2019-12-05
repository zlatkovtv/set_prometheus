package cecs429.query;

import cecs429.index.DiskPositionalIndex;
import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.index.ScorePosting;
import cecs429.text.TokenProcessor;
import cecs429.util.MathOperations;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class RankedQuery  {
    private String query;
    private int corpusSize;
    private int nonZeroAccumCount;

    public RankedQuery(String term, int corpusSize) {
        this.query = term;
        this.corpusSize = corpusSize;
        this.nonZeroAccumCount = 0;
    }

    public int getNonZeroAccumCount() {
        return this.nonZeroAccumCount;
    }

    public List<ScorePosting> getPostings(Index index, TokenProcessor tokenProcessor) {
        Map<Integer, Double> accumulators = new HashMap<>();
        try {
            String[] split = this.query.split(" ");
            for (String term: split) {
                term = tokenProcessor.processQueryToken(term);
                List<Posting> results = index.getRankedPostings(term);
                double wqt = Math.log(1 + ((double) corpusSize / results.size()));
                double accumulator = 0;
                for (Posting p: results) {
                    int tftd = p.getPositions().size();
                    double wdt = p.getScore();
                    accumulator += (wdt * wqt);
                    double ld = ((DiskPositionalIndex)index).getLd(p.getDocumentId() );
                    if(accumulator != 0) {
                        accumulator /= ld;
                    }

                    accumulator = MathOperations.roundUp(accumulator);

                    if(accumulators.containsKey(p.getDocumentId())) {
                        accumulators.put(p.getDocumentId(), (accumulators.get(p.getDocumentId()) + accumulator));
                    } else {
                        accumulators.put(p.getDocumentId(), accumulator);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        PriorityQueue<Map.Entry<Integer, Double>> priorityQueue = new PriorityQueue<>( new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> v1, Map.Entry<Integer, Double> v2) {
                if (v1.getValue() > v2.getValue()) return -1;
                if (v1.getValue() < v2.getValue()) return 1;
                return 0;
            }
        });

        for (Map.Entry<Integer, Double> pair: accumulators.entrySet()) {
            if(pair.getValue() != 0) {
                ++this.nonZeroAccumCount;
            }

            priorityQueue.offer(pair);
        }

        List<ScorePosting> topKResults = new ArrayList<>();
        int counter = 0;
        while(!priorityQueue.isEmpty()) {
            if(counter >= 50) {
                break;
            }
            Map.Entry<Integer, Double> pair = priorityQueue.poll();
            //Return two lists here.
            topKResults.add(new ScorePosting(pair.getKey(), pair.getValue()));
            ++counter;
        }

        return topKResults;
    }
}
