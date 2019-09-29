/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cecs429.index;

import cecs429.util.KGramIterator;
import java.util.*;

/**
 *
 * @author zack-laptop
 */
public class KGramIndex {
    private HashMap<String, List<String>> mapping = new HashMap<>();

    public void addTerm(String term, int documentId){
        List<String> kGrams = new ArrayList<>();
        StringBuilder sb = new StringBuilder(term);
        sb.insert(0, '$');
        sb.append('$');
        String processedTerm = sb.toString();
        Iterator i = new KGramIterator(1, processedTerm);
        while(i.hasNext()) {
            kGrams.add(i.next().toString());
        }

        i = new KGramIterator(2, processedTerm);
        while(i.hasNext()) {
            kGrams.add(i.next().toString());
        }

        i = new KGramIterator(3, processedTerm);
        while(i.hasNext()) {
            kGrams.add(i.next().toString());
        }

        // not adding duplicate grams or terms.
        for (String gram: kGrams) {
            List<String> candidates = mapping.getOrDefault(gram, Collections.emptyList());

            if (candidates.isEmpty()) {
                List<String> list = new ArrayList<>();
                list.add(term);
                mapping.put(gram, list);
            } else {
                if (!candidates.get(candidates.size() - 1).equals(term)) {
                    candidates.add(term);
                }
            }
        }
    }

//    private Set<String> generateKgrams(String sentence, int kgramCount) {
//        StringReader reader = new StringReader(sentence);
//        Set<String> ngrams = new HashSet<>();
//
//        //use lucene's shingle filter to generate the tokens
//        NGramTokenizer source = new NGramTokenizer(1, 3);
//        TokenFilter tokenStream = new ShingleFilter(source);
//        tokenStream.
//
//        if(kgramCount == 1){
//            sf = new StandardFilter(tokenStream);
//        }
//        else{
//            sf = new ShingleFilter(tokenStream);
//            ((ShingleFilter)sf).setMaxShingleSize(ngramCount);
//        }
//    }
    
    public List<String> getCandidates(String term) {
        return mapping.getOrDefault(term, Collections.emptyList());
    }
}
