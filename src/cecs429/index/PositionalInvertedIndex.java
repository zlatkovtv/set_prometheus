/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cecs429.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author zack-laptop
 */
public class PositionalInvertedIndex implements Index {

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
    public void addTerm(String term, int documentId, int position) {
        List<Posting> results = mapping.getOrDefault(term, Collections.emptyList());

        if (!results.isEmpty()) {
            if (results.get(results.size() - 1).getDocumentId() != documentId) {
                // same term, new document
                Posting p = new Posting(documentId);
                p.addPosition(position);
                results.add(p);
            } else {
                // same term, same document, new position
                Posting existingPosting = results.get(results.size() - 1);
                existingPosting.addPosition(position);
            }
        } else {
            // New term, new document
            List<Posting> newPosting = new ArrayList<>();
            Posting p = new Posting(documentId);
            p.addPosition(position);
            newPosting.add(p);
            mapping.put(term, newPosting);
        }
    }

    @Override
    public List<Posting> getPostings(String term) { return mapping.getOrDefault(term, Collections.emptyList()); }

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
