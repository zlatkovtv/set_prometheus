package cecs429.index;

import cecs429.util.VariableByteEncoder;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DiskIndexWriter {
    private List<String> vocabulary;
    private List<Long> postings;
    private List<Long> diskVocab;
    private Index index;
    private String path;
    private ArrayList<Double> ld;

    public DiskIndexWriter(Index indx, String absolutepath, ArrayList<Double> docWeight) {
        index = indx;
        path = absolutepath;
        diskVocab = new ArrayList<>();
        ld = docWeight;
    }

    public void writeIndex() throws IOException {
        File vocabFile = new File(path + "/Vocab.bin");
        File postingsFile = new File(path + "/Postings.bin");
        File tableFile = new File(path + "/VocabTable.bin");
        File docWeightFile = new File(path + "/docWeights.bin");

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
            createVocabTableBin();
            System.out.println("Finished creating table.bin");
            createdocWeightBin();
            System.out.println("Finished creating docWeight.bin");
        }
    }

    private void createdocWeightBin() throws IOException {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(path + "/docWeights.bin"));
        for (double weight: ld ) {
            out.writeDouble(weight);
        }

        out.close();
    }

    private List<Long> createPostingBin() throws IOException {
        List<Long> addresses = new ArrayList<>();
        DataOutputStream out = new DataOutputStream(new FileOutputStream(path + "/Postings.bin"));
        addresses.add(0l);
        for (String term : vocabulary) {
            int docIdGap = 0;
            int prevDocId = 0;
            List<Posting> posting = index.getPostings(term);
            //write dft how many documents this posting has
            writeVBInt(out, posting.size());
            for (Posting p : posting) {
                docIdGap = p.getDocumentId() - prevDocId;
                prevDocId = p.getDocumentId();
                writeVBInt(out, docIdGap);
                //compute DSP here
                int tftd = p.getPositions().size();
                double wdt = 1.0;
                if(tftd != 0) {
                    wdt = 1 + Math.log(tftd);
                }

                wdt = roundUp(wdt);
                out.writeDouble(wdt);
                writeVBInt(out, p.getPositions().size());
                int postingGap = 0;
                int prevPostingId = 0;
                for (Integer i : p.getPositions()) {
                    postingGap = i - prevPostingId;
                    writeVBInt(out, postingGap);
                    prevPostingId = i;
                }
            }

            addresses.add(Long.valueOf(out.size()));
        }

        out.close();
        return addresses;
    }

    private void writeVBInt(DataOutputStream out, int number) throws IOException {
        List<Long> bytes = VariableByteEncoder.encode(Long.valueOf(number));
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
            out.write(term.getBytes("UTF-8"), 0, term.getBytes().length);
            diskVocab.add(length);
            length += term.getBytes("UTF-8").length;
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

    private double roundUp(double input) {
        BigDecimal bd = new BigDecimal(Double.toString(input));
        bd = bd.setScale(5, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
