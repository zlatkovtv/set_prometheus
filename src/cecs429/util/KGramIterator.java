package cecs429.util;

import java.util.Iterator;

public class KGramIterator implements Iterator<String> {
    private final String str;
    private final int n;
    int pos = 0;

    public KGramIterator(int n, String str) {
        this.n = n;
        this.str = str;
    }

    public boolean hasNext() {
        return pos < str.length() - n + 1;
    }

    public String next() {
        return str.substring(pos, pos++ + n);
    }
}