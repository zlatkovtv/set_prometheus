package cecs429.main;

import cecs429.index.DiskIndexWriter;
import cecs429.index.Posting;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/*
Please use UIMain as this is just for quickly running and debugging
 */

public class ConsoleMain {
    private static final String path = "C:\\Users\\Memphis\\Desktop\\Projects\\CSULB\\SET\\set_prometheus\\moby-dick\\json";
    private static Scanner reader = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
//        BetterTermDocumentIndexer indexer = new BetterTermDocumentIndexer();
//        indexer.runIndexer(Paths.get(path).toAbsolutePath());
//        String input;
//        while (true) {
//            System.out.printf("");
//            input = reader.nextLine().toLowerCase();
//            switch (input) {
//                case "quit":
//                    System.exit(0);
//                    break;
//                default:
//                    indexer.selectQuery()
//                    List<Integer> results = indexer.runQuery(input);
//                    for (Integer docId : results) {
//                        System.out.println("Document ID " + docId);
//                    }
//
//                    System.out.println("Total: " + results.size());
//                    break;
//            }
//        }
//

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

                    List<Posting> results = new ArrayList<>();
                    if (mode.startsWith("1")) {
                        results = indexer.getResults(query, path);
                    } else if (mode.startsWith("2")) {
                       results = indexer.getRankedResults(query, path);
                    }

//                    Set<Integer> set = new HashSet<>(docIds);
//                    docIds.clear();
//                    docIds.addAll(set);
//                    Collections.sort(docIds);

                    for (Posting p : results) {
                        System.out.println("Document ID " + p.getDocumentId());
                        System.out.println("Document Score " + p.getScore());
                    }

                    System.out.println("Total: " + results.size());

                    break;
                default:
                    System.out.println("Please choose a valid option");
                    break;
            }
        }
    }
}
