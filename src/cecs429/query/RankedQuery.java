package cecs429.query;

import cecs429.index.DiskPositionalIndex;
import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.TokenProcessor;

import java.io.IOException;
import java.util.*;

public class RankedQuery implements QueryComponent {
    private String query;
    private int corpusSize;

    public RankedQuery(String term, int corpusSize) {
        this.query = term;
        this.corpusSize = corpusSize;
    }

    @Override
    public List<Posting> getPostings(Index index, TokenProcessor tokenProcessor) {
        Map<Integer, Double> accumulators = new HashMap<>();
        try {
            String[] split = this.query.split(" ");
            for (String term: split) {
                term = tokenProcessor.processQueryToken(term);
                List<Posting> results = index.getRankedPostings(term);
                double wqt = Math.log(1 + (corpusSize / results.size()));
                double accumulator = 0;
                for (Posting p: results) {
                    int tftd = p.getPositions().size();
                    double wdt = 1;
                    if(tftd != 0) {
                        wdt = 1 + Math.log(tftd);
                    }

                    accumulator += (wdt * wqt);
                    double ld = ((DiskPositionalIndex)index).getLd(p.getDocumentId() - 1);
                    if(accumulator != 0) {
                        accumulator /= ld;
                    }

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
            priorityQueue.offer(pair);
        }

        List<Posting> topKResults = new ArrayList<>();
        int counter = 0;
        while(!priorityQueue.isEmpty()) {
            if(counter >= 10) {
                break;
            }
            Map.Entry<Integer, Double> pair = priorityQueue.poll();
            topKResults.add(new Posting(pair.getKey(), pair.getValue()));
            ++counter;
        }

        return topKResults;
    }
}
