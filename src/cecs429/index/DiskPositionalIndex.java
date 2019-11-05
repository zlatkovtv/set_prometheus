package cecs429.index;

import cecs429.util.VariableByteEncoder;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DiskPositionalIndex implements Index {
    private String mPath;
    private RandomAccessFile mVocabList;
    private RandomAccessFile mPostings;
    private RandomAccessFile mDocumentWeight;
    private long[] mVocabTable;

    // Opens a disk inverted index that was constructed in the given path.
    public DiskPositionalIndex(String path) {
        try {
            mPath = path;
            mVocabList = new RandomAccessFile(new File(path, "Vocab.bin"), "r");
            mPostings = new RandomAccessFile(new File(path, "Postings.bin"), "r");
            mDocumentWeight = new RandomAccessFile(new File(path, "docWeights.bin"), "r");
            mVocabTable = readVocabTable(path);
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        }
    }

    // Locates the byte position of the postings for the given term.
    // For example, binarySearchVocabulary("angel") will return the byte position
    // to seek to in postings.bin to find the postings for "angel".
    private long binarySearchVocabulary(String term) {
        // do a binary search over the vocabulary, using the vocabTable and the file vocabList.
        int i = 0, j = mVocabTable.length / 2 - 1;
        while (i <= j) {
            try {
                int m = (i + j) / 2;
                long vListPosition = mVocabTable[m * 2];
                int termLength;
                if (m == mVocabTable.length / 2 - 1) {
                    termLength = (int) (mVocabList.length() - mVocabTable[m * 2]);
                } else {
                    termLength = (int) (mVocabTable[(m + 1) * 2] - vListPosition);
                }

                mVocabList.seek(vListPosition);

                byte[] buffer = new byte[termLength];
                mVocabList.read(buffer, 0, termLength);
                String fileTerm = new String(buffer, "UTF-8");
                int compareValue = term.compareTo(fileTerm);
                if (compareValue == 0) {
                    // found it!
                    return mVocabTable[m * 2 + 1];
                } else if (compareValue < 0) {
                    j = m - 1;
                } else {
                    i = m + 1;
                }
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
        }
        return -1;
    }

    // Reads the file vocabTable.bin into memory.
    private static long[] readVocabTable(String indexName) {
        try {
            long[] vocabTable;

            RandomAccessFile tableFile = new RandomAccessFile(
                    new File(indexName, "VocabTable.bin"),
                    "r");

            int tableIndex = 0;
            vocabTable = new long[(int) tableFile.length() / 16 * 2];
            byte[] byteBuffer = new byte[8];

            while (tableFile.read(byteBuffer, 0, byteBuffer.length) > 0) { // while we keep reading 4 bytes
                vocabTable[tableIndex] = ByteBuffer.wrap(byteBuffer).getLong();
                tableIndex++;
            }
            tableFile.close();
            return vocabTable;
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
        return null;
    }

    public int getTermCount() {
        return mVocabTable.length / 2;
    }

    @Override
    public List<Posting> getPostings(String term) {
        long bytePosition = binarySearchVocabulary(term);
        List<Posting> postings = new ArrayList<>();

        try {
            mPostings.seek(bytePosition);
            long numberOfDocuments = readVBLong();
            int docIdGap = 0;
            for (int i = 0; i < numberOfDocuments; i++) {
                docIdGap += readVBLong();
                Posting posting = new Posting(docIdGap, new ArrayList<>());
                double wdt = mPostings.readDouble();
                long numberOfPositions = readVBLong();

                int currentPosition = 0;
                for (int j = 0; j < numberOfPositions; j++) {
                    currentPosition += readVBLong();
                    posting.addPosition(currentPosition);
                }

                postings.add(posting);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return postings;
    }

    @Override
    public List<Posting> getRankedPostings(String term) {
        long bytePosition = binarySearchVocabulary(term);
        List<Posting> postings = new ArrayList<>();

        try {
            mPostings.seek(bytePosition);
            long numberOfDocuments = readVBLong();
            int docIdGap = 0;
            for (int i = 0; i < numberOfDocuments; i++) {
                docIdGap += readVBLong();
                Posting posting = new Posting(docIdGap, new ArrayList<>());
                double wdt = mPostings.readDouble();
                posting.setScore(wdt);
                long numberOfPositions = readVBLong();

                int currentPosition = 0;
                for (int j = 0; j < numberOfPositions; j++) {
                    currentPosition += readVBLong();
//                    posting.addPosition(currentPosition);
                }

                postings.add(posting);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return postings;
    }

    @Override
    public List<String> getVocabulary() {
        return null;
    }

    private long readVBLong() throws IOException {
        long encode = 0;
        List<Long> encoded = new ArrayList<Long>();

        do {
            encode = mPostings.read();
            encoded.add(encode);
        } while (encode < 128);

        // Genius way of decoding.
        return VariableByteEncoder.decode(encoded).get(0);
    }

    public double getLd(int bytePosition) throws IOException {
        mDocumentWeight.seek(bytePosition * 8);
        return mDocumentWeight.readDouble();
    }
}
