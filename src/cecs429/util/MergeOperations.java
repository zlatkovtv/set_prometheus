package cecs429.util;

import cecs429.index.Posting;

import java.util.ArrayList;
import java.util.List;

public class MergeOperations {
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
