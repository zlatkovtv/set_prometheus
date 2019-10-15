package cecs429.main;

import cecs429.index.DiskIndexWriter;
import cecs429.index.Index;
import cecs429.index.PositionalInvertedIndex;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/*
Please use UIMain as this is just for quickly running and debugging
 */

public class ConsoleMain {
    private static final String path = "C:\\Users\\zack\\Documents\\set_prometheus\\moby-dick";
    private static Scanner reader = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        //Index index =  new PositionalInvertedIndex();
        BetterTermDocumentIndexer indexer = new BetterTermDocumentIndexer();
        indexer.runIndexer(Paths.get(path).toAbsolutePath());
        DiskIndexWriter diskIndexer = new DiskIndexWriter(indexer.getIndex(), Paths.get(path).toAbsolutePath());
        diskIndexer.WriteIndex();
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

                    System.out.println("Total: " + results.size());
                    break;
            }
        }
    }
}
