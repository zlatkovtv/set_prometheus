package cecs429.main;

import cecs429.documents.DocumentCorpus;
import cecs429.index.DiskIndexWriter;
import cecs429.index.Posting;
import cecs429.index.ScorePosting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/*
Please use UIMain as this is just for quickly running and debugging
 */

public class ConsoleMain {
    private static final String path = "C:\\Users\\018092397SA\\Desktop\\Projects\\ml3-cranfield";
    private static Scanner reader = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        BetterTermDocumentIndexer indexer = new BetterTermDocumentIndexer(Paths.get(path).toAbsolutePath());
        DocumentCorpus corpus = indexer.getCorpus();
        corpus.getDocuments();

        while (true) {
            System.out.println("------------------------------");
            System.out.println(":q - To quit application");
            System.out.println("1. Build index.");
            System.out.println("2. Query index.");
            System.out.println("3. MAP calculations");
            System.out.print("\n\nPlease make a selection: ");

            String input = reader.nextLine().toLowerCase();
            String mode = "";
            switch (input) {
                case ":q":
                    System.exit(0);
                    break;
                case "1":
                    indexCorpus(indexer);
                    break;
                case "2":
                    System.out.println("\n1. Boolean retrieval.");
                    System.out.println("2. Ranked retrieval.");
                    System.out.print("\n\nPlease make a selection: ");
                    mode = reader.nextLine();

                    System.out.print("\n\nPlease enter search term: ");
                    String query = reader.nextLine();

                    if (mode.startsWith("1")) {
                        queryBoolean(indexer, query);
                    } else if (mode.startsWith("2")) {
                        queryRanked(indexer, query);
                    }

                    break;
                case "3":
                    List<String> queries = readQueries(path + "\\relevance\\queries");
                    List<String> booleanQueries = readQueries(path + "\\relevance\\booleanQueries");
                    List<List<Integer>> qrelCranefield = readQrel(path + "\\relevance\\qrel");

                    System.out.println("\n1. Boolean retrieval.");
                    System.out.println("2. Ranked retrieval.");
                    System.out.print("\n\nPlease make a selection: ");
                    mode = reader.nextLine();

                    if (mode.startsWith("1")) {
                        mapBoolean(indexer, booleanQueries);
                        mapBoolean(indexer, queries);
                    } else if (mode.startsWith("2")) {
                        mapRanked(indexer, corpus, queries, qrelCranefield);
                    }

                    break;
                default:
                    System.out.println("Please choose a valid option");
                    break;
            }
        }
    }

    private static void queryRanked(BetterTermDocumentIndexer indexer, String query) {
        List<ScorePosting> results = new ArrayList<>();
        results = indexer.getScoreResults(query, path);
        for (ScorePosting p : results) {
            System.out.println("Document ID " + p.getDocumentId());
            System.out.println("Document Score " + p.getAccumulator());
        }

        System.out.println("Total: " + results.size());
    }

    private static void queryBoolean(BetterTermDocumentIndexer indexer, String query) {
        try {
            List<Posting> results = indexer.getResults(query, path);
            List<Integer> docIds = results.stream()
                    .map(x -> x.getDocumentId())
                    .collect(Collectors.toList());

            Set<Integer> set = new HashSet<>(docIds);
            docIds.clear();
            docIds.addAll(set);
            Collections.sort(docIds);

            for (Integer p : docIds) {
                System.out.println("Document ID " + p);
            }

            System.out.println("Total: " + docIds.size());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void mapBoolean(BetterTermDocumentIndexer indexer, List<String> booleanQueries) {
        List<Double> indexRunTimes = new ArrayList<>();
        for (int i = 0; i < booleanQueries.size(); i++) {
            String q = booleanQueries.get(i).trim();
            long startTime = System.currentTimeMillis();
            List<Posting> results = indexer.getResults(q, path);
            long endTime = System.currentTimeMillis();
            indexRunTimes.add(((endTime - startTime) / 1000.00));
        }

        double sumRunTimes = getSum(indexRunTimes);

        System.out.println("The Mean Response Time to Satisfy a Boolean Query (5 simplified queries created by hand): " + ((double) sumRunTimes / booleanQueries.size()));
        System.out.println("The Throughput to Satisfy a Boolean Query (5 simplified queries created by hand): " + (1.0 / (sumRunTimes / booleanQueries.size())));
    }

    private static void mapRanked(BetterTermDocumentIndexer indexer, DocumentCorpus corpus, List<String> queries, List<List<Integer>> qrelCranefield) {
        List<Integer> docIds;
        List<Double> averagePs = new ArrayList<>();
        String q = queries.get(0);
        List<ScorePosting> results = new ArrayList<>();
        List<Double> indexRunTimes = new ArrayList<>();

        ///////////////////////////////////////////////////////////////////////////////////////////////
        for (int i = 0; i < 30; i++) {
            long startTime = System.currentTimeMillis();
            results = indexer.getScoreResults(q, path);
            long endTime = System.currentTimeMillis();
            indexRunTimes.add( ((endTime - startTime) / 1000.00));
        }

        double sumRunTimes = 0;
        for (double indx : indexRunTimes) {
            sumRunTimes += indx;
        }

        docIds = results.stream()
                .map(x -> x.getDocumentId())
                .collect(Collectors.toList());

        List<Integer> qrelRow = qrelCranefield.get(0);
        int acc = 0;
        double ap = 0;
        for (int j = 0; j < docIds.size(); j++) {
            if (qrelRow.contains(docIds.get(j) + 1)) {
                System.out.print("Relevant");
                ++acc;
                ap += (double) acc / (j + 1);
            } else {
                System.out.print("Not Relevant");
            }

            System.out.println( ": " + corpus.getDocument(docIds.get(j)).getTitle());
        }

        ap = ap / qrelRow.size();
        System.out.println("\nAverage Throughput for first query in Cranfield collection 30 times: " + (1.0 / (sumRunTimes / 30.0)));
        System.out.println("Average Precision: " + ap);
        ///////////////////////////////////////////////////////////////////////////////////////////////
        indexRunTimes = new ArrayList<>();
        int nonZeroAccums = 0;
        for (int i = 0; i < queries.size(); i++) {
            q = queries.get(i);
            long startTime = System.currentTimeMillis();
            results = indexer.getScoreResults(q, path);
            long endTime = System.currentTimeMillis();
            indexRunTimes.add(((endTime - startTime) / 1000.00));
            nonZeroAccums += indexer.getNonZeroAccumCount();

            docIds = results.stream()
                    .map(x -> x.getDocumentId())
                    .collect(Collectors.toList());
            qrelRow = qrelCranefield.get(i);
            acc = 0;
            ap = 0;
            for (int j = 0; j < docIds.size(); j++) {
                if (qrelRow.contains(docIds.get(j) + 1)) {
                    ++acc;
                    ap += (double) acc / (j + 1);
                }
            }

            ap = ap / qrelRow.size();
            averagePs.add(ap);

        }

        sumRunTimes = getSum(indexRunTimes);

        double sum = averagePs.stream().mapToDouble(i -> i).sum();
        double map = sum / averagePs.size();
        System.out.println("\nThe MAP for Cranfield collection : " + map);
        System.out.println("The average number of nonzero accumulators used in the queries for Cranfield collection: " + (nonZeroAccums/queries.size()));
        System.out.println("The Mean Response Time to Satisfy a Ranked Query: " + ((double) sumRunTimes / queries.size()));
        System.out.println("The Throughput to Satisfy a Ranked Query (All Queries in Cranfield collection): " + (1.0 / (sumRunTimes / queries.size())));
    }

    private static void indexCorpus(BetterTermDocumentIndexer indexer) throws IOException {
        indexer.runIndexer();
        DiskIndexWriter writer = new DiskIndexWriter(indexer.getIndex(), path, indexer.getDocWeight());
        writer.writeIndex();
    }

    private static double getSum(List<Double> indexRunTimes) {
        double sumRunTimes = 0;
        for (double indx : indexRunTimes) {
            sumRunTimes += indx;
        }

        return sumRunTimes;
    }

    private static List<String> readQueries(String path) {
        List<String> result = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            while (line != null) {
                result.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println("OK");
        return result;
    }

    private static List<List<Integer>> readQrel(String path) {
        List<List<Integer>> result = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            while (line != null) {
                String[] s = line.split("\\s+");
                List<Integer> inner = new ArrayList<>();
                for (String part : s) {
                    inner.add(Integer.parseInt(part));
                }

                result.add(inner);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println("OK");
        return result;
    }
}