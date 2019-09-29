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
public class KGramIndex implements Index {

    private HashMap<String, List<Posting>> mapping = new HashMap<>();

    public void addTerm(String term, int documentId){
        List<String> kGrams = new ArrayList<>();
        StringBuilder sb = new StringBuilder(term);
        sb.insert(0, '$');
        sb.append('$');
        term = sb.toString();
        Iterator i = new KGramIterator(1, term);
        while(i.hasNext()) {
            kGrams.add(i.next().toString());
        }

        i = new KGramIterator(2, term);
        while(i.hasNext()) {
            kGrams.add(i.next().toString());
        }

        i = new KGramIterator(3, term);
        while(i.hasNext()) {
            kGrams.add(i.next().toString());
        }

        // not adding duplicate grams or terms.
        for (String gram: kGrams) {
            List<Posting> results = mapping.getOrDefault(gram, Collections.emptyList());

            if (results.isEmpty()) {
                List<Posting> list = new ArrayList<>();
                Posting p = new Posting(documentId);
                list.add(p);
                mapping.put(gram, list);
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
    
    @Override
    public List<Posting> getPostings(String term) {
        return mapping.getOrDefault(term, Collections.emptyList());
    }
    
    @Override
    public List<String> getVocabulary() {
        List<String> mVocabulary = new ArrayList<>();
        
        for (String term : mapping.keySet()) {
            mVocabulary.add(term);
        }
        Collections.sort(mVocabulary);
        return Collections.unmodifiableList(mVocabulary);
    }
}
