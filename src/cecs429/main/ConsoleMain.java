package cecs429.main;

import cecs429.index.DiskIndexWriter;
import cecs429.index.Posting;
import cecs429.index.ScorePosting;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/*
Please use UIMain as this is just for quickly running and debugging
 */

public class ConsoleMain {
    private static final String path = "C:\\Users\\Memphis\\Desktop\\Projects\\CSULB\\SET\\Homework3\\assets\\split";
    private static Scanner reader = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        System.out.println(":q - To quit application");
        System.out.println("1. Build index.");
        System.out.println("2. Query index.");
        System.out.print("\n\nPlease make a selection: ");

        BetterTermDocumentIndexer indexer = new BetterTermDocumentIndexer(Paths.get(path).toAbsolutePath());

        while (true) {
            String input = reader.nextLine().toLowerCase();
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
                    String mode = reader.nextLine();
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
                default:
                    System.out.println("Please choose a valid option");
                    break;
            }
        }
    }
}
