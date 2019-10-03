/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cecs429.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zack-laptop
 */
public class InvertedIndex implements Index {

    private HashMap<String, List<Posting>> mapping = new HashMap<>();

    /**
     * Constructs an empty index with with given vocabulary set and corpus size.
     *
     * @param vocabulary a collection of all terms in the corpus vocabulary.
     * @param corpuseSize the number of documents in the corpus.
     */
    /**
     * Associates the given documentId with the given term in the index.
     */
    public void addTerm(String term, int documentId) {
        List<Posting> results = mapping.getOrDefault(term, Collections.emptyList());

        if (!results.isEmpty()) {
            if (results.get(results.size() - 1).getDocumentId() != documentId) {
                results.add(new Posting(documentId));
            }
        } else {
            List<Posting> newPosting = new ArrayList<>();
            newPosting.add(new Posting(documentId));
            mapping.put(term, newPosting);
        }
    }

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
