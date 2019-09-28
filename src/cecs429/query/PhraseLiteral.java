package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.TokenProcessor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a phrase literal consisting of one or more terms that must occur in sequence.
 */
public class PhraseLiteral implements QueryComponent {
    // The list of individual terms in the phrase.
    private List<String> mTerms = new ArrayList<>();

    /**
     * Constructs a PhraseLiteral with the given individual phrase terms.
     */
    public PhraseLiteral(List<String> terms) {
        mTerms.addAll(terms);
    }

    /**
     * Constructs a PhraseLiteral given a string with one or more individual terms separated by spaces.
     */
    public PhraseLiteral(String terms) {
        mTerms.addAll(Arrays.asList(terms.split(" ")));
    }

    @Override
    public List<Posting> getPostings(Index index, TokenProcessor processor) {
        // normalize tokens
        List<String> normalizedTokens = new ArrayList<>(mTerms.stream().map(t -> processor.processQueryToken(t)).collect(Collectors.toList()));
        Iterator<String> terms = normalizedTokens.iterator();

        List<Posting> result = index.getPostings(terms.next()); // 0, 1, 3

        while (terms.hasNext()) {
            result = postionalIntersect(result, index.getPostings(terms.next()), 1);
        }

        return result;
    }

    /*
    - Author(s) name (Individual or corporation): anwar
    - 9/28/2019
    - PHRASE QUERIES AND POSITIONAL INDEXES
    - Code version -N/A
    - Slides
    - https://web.cs.dal.ca/~anwar/ir/lecturenotes/l9.pdf
    */

    public List<Posting> postionalIntersect(List<Posting> p1, List<Posting> p2, int k) {
        List<Posting> answer = new ArrayList<>();
        int itr = 0;
        int jtr = 0;

        while (itr < p1.size() && jtr < p2.size()) {
            if (p1.get(itr).getDocumentId() == p2.get(jtr).getDocumentId()) {
                List<Integer> l = new ArrayList<>();
                List<Integer> pp1 = p1.get(itr).getPositions();
                List<Integer> pp2 = p2.get(jtr).getPositions();
                int ip = 0;
                int jp = 0;
                while (ip < pp1.size()) {
                    while (jp < pp2.size()) {
                        if ((pp2.get(jp) - pp1.get(ip)) <= k) {
                            l.add(pp2.get(jp));

                        } else if (pp2.get(jp) > pp1.get(ip)) {
                            break;
                        }
                        ++jp;

                    }

                    while (l.size() > 0 && (pp1.get(ip) - l.get(0)) >= k) {
                        l.remove(0);
                    }
                    for (Integer ps : l) {
                        ArrayList<Integer> tmp2 = new ArrayList<>();
                        tmp2.add(pp1.get(ip));
                        tmp2.add(ps);
                        Posting p = new Posting(p1.get(itr).getDocumentId(), tmp2);

                        answer.add(p);

                    }
                    ++ip;

                }
                ++itr;
                ++jtr;
            } else if (p1.get(itr).getDocumentId() < p2.get(jtr).getDocumentId()) {
                ++itr;
            } else {
                ++jtr;
            }
        }

        return answer;
    }


    @Override
    public String toString() {
        return "\"" + String.join(" ", mTerms) + "\"";
    }
}
