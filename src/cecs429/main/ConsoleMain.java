package cecs429.main;

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
    private static final String path = "C:\\Users\\Memphis\\Desktop\\Projects\\CSULB\\SET\\Homework3\\assets\\ml3-cranfield";
    private static Scanner reader = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        System.out.println(":q - To quit application");
        System.out.println("1. Build index.");
        System.out.println("2. Query index.");
        System.out.println("3. MAP calculations");
        System.out.print("\n\nPlease make a selection: ");

        BetterTermDocumentIndexer indexer = new BetterTermDocumentIndexer(Paths.get(path).toAbsolutePath());

        while (true) {
            String input = reader.nextLine().toLowerCase();
            String mode ="";
            switch (input) {
                case ":q":
                    System.exit(0);
                    break;
                case "1":
                    indexer.runIndexer();
                    DiskIndexWriter writer = new DiskIndexWriter(indexer.getIndex(), path, indexer.getDocWeight());
                    writer.writeIndex();
                    break;
                case "2":
                    System.out.println("\n1. Boolean retrieval.");
                    System.out.println("2. Ranked retrieval.");
                    System.out.print("\n\nPlease make a selection: ");
                    mode = reader.nextLine();
                    System.out.print("\n\nPlease enter search term: ");
                    String query = reader.nextLine();

                    if (mode.startsWith("1")) {
                        List<Posting> results = new ArrayList<>();
                        try {
                            results = indexer.getResults(query, path);

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
                        } catch(Exception e) {
                            System.out.println(e.getMessage());
                        }
                    } else if (mode.startsWith("2")) {
                        List<ScorePosting> results = new ArrayList<>();
                        results = indexer.getScoreResults(query, path);
                        for (ScorePosting p : results) {
                            System.out.println("Document ID " + p.getDocumentId());
                            System.out.println("Document Score " + p.getAccumulator());
                        }

                        System.out.println("Total: " + results.size());
                    }

                    break;
                case "3":
                    List<String> queries = readQueries(path + "\\relevance\\queries");
                    List<List<Integer>> qrelCranefield = readQrel(path + "\\relevance\\qrel");

                    System.out.println("\n1. Boolean retrieval.");
                    System.out.println("2. Ranked retrieval.");
                    System.out.print("\n\nPlease make a selection: ");
                    mode = reader.nextLine();

                    List<Integer> docIds;
                    if (mode.startsWith("1")) {
                        for (int i = 0; i < queries.size(); i++) {
                            String q = queries.get(i);

                            List<Posting> results = new ArrayList<>();
                            try {
                                results = indexer.getResults(q, path);

                                docIds = results.stream()
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

                            List<Integer> qrelRow = qrelCranefield.get(i);
                        }
                    } else if (mode.startsWith("2")) {
                        List<Double> averagePs = new ArrayList<>();
                        for (int i = 0; i < queries.size(); i++) {
                            String q = queries.get(i);
                            List<ScorePosting> results = new ArrayList<>();
                            results = indexer.getScoreResults(q, path);
                            for (ScorePosting p : results) {
                                System.out.println("Document ID " + p.getDocumentId());
                                System.out.println("Document Score " + p.getAccumulator());
                            }

                            System.out.println("Total: " + results.size());

                            docIds = results.stream()
                                    .map(x -> x.getDocumentId())
                                    .collect(Collectors.toList());
                            List<Integer> qrelRow = qrelCranefield.get(i);
                            int acc = 0;
                            double ap = 0;
                            for (int j = 0; j < qrelRow.size(); j++) {
                                if(docIds.get(j) == qrelRow.get(j)) {
                                    ++acc;
                                    ap += (double) acc/(j+1);
                                }
                            }

                            ap = ap / qrelRow.size();
                            averagePs.add(ap);
                        }

                        double sum = averagePs.stream().mapToDouble(i -> i).sum();
                        double map = sum/averagePs.size();
                    }
                    break;
                default:
                    System.out.println("Please choose a valid option");
                    break;
            }
        }
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

        System.out.println("OK");
        return result;
    }

    private static List<List<Integer>> readQrel(String path) {
        List<List<Integer>> result = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            while (line != null) {
                String[] s = line.split(" ");
                List<Integer> inner = new ArrayList<>();
                for (String part: s) {
                    inner.add(Integer.parseInt(part));
                }

                result.add(inner);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("OK");
        return result;
    }
}
