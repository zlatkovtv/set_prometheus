package cecs429.index;

import cecs429.util.VariableByteEncoder;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DiskIndexWriter {
    private List<String> vocabulary;
    private List<Long> postings;
    private List<Long> diskVocab;
    private Index index;
    private String path;

    public DiskIndexWriter(Index indx, String absolutepath) {
        index = indx;
        path = absolutepath;
        diskVocab = new ArrayList<>();
    }

    public void writeIndex() throws IOException {
        File vocabFile = new File(path + "/Vocab.bin");
        File postingsFile = new File(path + "/Postings.bin");
        File tableFile = new File(path + "/VocabTable.bin");

        if(!vocabFile.exists() || !postingsFile.exists()|| !tableFile.exists() ) {
            vocabFile.delete();
            postingsFile.delete();
            tableFile.delete();
            //create posting.bin -> returns/fill in data structure that helps you remmebr the bystes postion (a long)
            vocabulary = index.getVocabulary();

            // write each term in the index vocabulary to vocab,bin -> return/fill in the byte position
            postings = createPostingBin();
            System.out.println("Finished creating postings.bin");
            createVocabBin();
            System.out.println("Finished creating vocab.bin");
            //vocabTable.bin writing two lomg values
            createVocabTableBin();
            System.out.println("Finished creating table.bin");
        }

    }

    private List<Long> createPostingBin() throws IOException {
        List<Long> addresses = new ArrayList<>();
        DataOutputStream out = new DataOutputStream(new FileOutputStream(path + "/Postings.bin"));
        addresses.add(0l);
        for (String term : vocabulary) {
            int docIdGap = 0;
            List<Posting> posting = index.getPostings(term);
            //write dft how many documents this posting has
            writeVBInt(out, posting.size());
            for (Posting p : posting) {
                docIdGap = p.getDocumentId() - docIdGap;
                writeVBInt(out, docIdGap);
                writeVBInt(out, p.getPositions().size());

                int postingGap = 0;
                for (Integer i : p.getPositions()) {
                    postingGap = i - postingGap;
                    writeVBInt(out, postingGap);

                }
            }
            addresses.add(Long.valueOf(out.size()));
        }

        out.close();
        return addresses;
    }

    private void writeVBInt(DataOutputStream out, int number) throws IOException {
        List<Long> bytes = VariableByteEncoder.VBEncodenumber(Long.valueOf(number));
        for (long b: bytes) {
            byte[] ba = new byte[1];
            ba[0] = (byte) b;
            out.write(ba, 0, 1);
        }
    }

    private void createVocabBin() throws IOException {
        long length = 0;
        DataOutputStream out = new DataOutputStream(
                new FileOutputStream(path + "/Vocab.bin"));
        for (String term:vocabulary) {
            out.writeBytes(term);
            diskVocab.add(length);
            length += term.length();
        }

        out.close();
    }

    private void createVocabTableBin() throws IOException {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(path + "/VocabTable.bin"));
        Iterator<Long> posting = postings.iterator();
        Iterator<Long> diskIterator = diskVocab.iterator();

        while(posting.hasNext() && diskIterator.hasNext()) {
            out.writeLong(diskIterator.next());
            out.writeLong(posting.next());
        }

        out.close();
    }
}
