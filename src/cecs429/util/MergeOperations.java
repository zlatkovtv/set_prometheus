package cecs429.util;

import cecs429.index.Posting;

import java.util.ArrayList;
import java.util.List;

public class MergeOperations {
        /*
    - Author(s) name (Individual or corporation): anwar
    - 9/28/2019
    - PHRASE QUERIES AND POSITIONAL INDEXES
    - Code version -N/A
    - Slides
    - https://web.cs.dal.ca/~anwar/ir/lecturenotes/l9.pdf
    */

    public static List<Posting> postionalIntersect(List<Posting> p1, List<Posting> p2, int k) {
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

    public static List<Posting> unionMerge(List<Posting> pList1, List<Posting> pList2) {
        // Reference https://www.geeksforgeeks.org/two-pointers-technique/
        // FYI The logic here is the same as a leet code question
        int i = 0, j = 0;
        List<Posting> tmp = new ArrayList<>();

        int pList1Size = pList1.size();
        int pList2Size = pList2.size();

        //while both lists are inbounds
        while (i < pList1Size && j < pList2Size) {
            if (pList1.get(i).getDocumentId() == pList2.get(j).getDocumentId()) {
                tmp.add(pList1.get(i));
                ++i;
                ++j;
            } else if (pList1.get(i).getDocumentId() < pList2.get(j).getDocumentId()) {
                tmp.add(pList1.get(i));
                ++i;
            } else if (pList2.get(j).getDocumentId() < pList1.get(i).getDocumentId()) {
                tmp.add(pList2.get(j));
                ++j;
            }
        }

        //Add the rest of either list if lists are not the same size
        if (pList1Size != pList2Size) {
            if (i == pList1.size()) {
                //add the rest of list2
                tmp.addAll(pList2.subList(j, pList2Size));
            } else if (j == pList2.size()) {
                //add the rest of list1
                tmp.addAll(pList1.subList(i, pList1Size));
            }
        }

        return tmp;
    }

    public static List<Posting> intersectMerge(List<Posting> pList1, List<Posting> pList2) {
        // Reference https://www.geeksforgeeks.org/two-pointers-technique/
        //This is written similar to how Dr. Terrel wrote on the board. I tried to mimic it as much
        //Declare "Pointers"
        int itr = 0;
        int jtr = 0;
        List<Posting> tmp = new ArrayList<>(); //Temporary list to hold resultes

        int pList1Size = pList1.size();
        int pList2Size = pList2.size();

        while (itr < pList1Size && jtr < pList2Size) {
            if (pList1.get(itr).getDocumentId() == pList2.get(jtr).getDocumentId()) {
                tmp.add(pList1.get(itr));
                ++itr;
                ++jtr;
            } else if (pList1.get(itr).getDocumentId() < pList2.get(jtr).getDocumentId()) {
                ++itr;
            } else if (pList2.get(jtr).getDocumentId() < pList1.get(itr).getDocumentId()) {
                ++jtr;
            }
        }

        return tmp;

    }
}
