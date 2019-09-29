package cecs429.main;

import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class ConsoleMain {
    private static final String path = "C:\\Users\\Memphis\\Desktop\\Projects\\SET\\Homework3\\assets\\partial";
    private static Scanner reader = new Scanner(System.in);

    public static void main(String[] args) {
        BetterTermDocumentIndexer indexer = new BetterTermDocumentIndexer();
        indexer.runIndexer(Paths.get(path).toAbsolutePath());
        String input;
        while (true) {
            System.out.printf("Enter search term or quit to exit. ");
            input = reader.nextLine().toLowerCase();
            switch (input) {
                case "quit":
                    System.exit(0);
                    break;
                default:
                    List<Integer> results = indexer.runQuery(input);
                    for (Integer docId : results) {
                        System.out.println("Document ID " + docId);
                    }

                    break;
            }
        }
    }
}
