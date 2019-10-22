package cecs429.index;

import java.util.List;

public class DiskPositionalIndex implements Index {
    @Override
    public List<Posting> getPostings(String term) {
        return null;
    }

    @Override
    public List<String> getVocabulary() {
        return null;
    }
}
