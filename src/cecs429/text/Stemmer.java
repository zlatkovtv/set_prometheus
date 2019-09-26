package cecs429.text;

import cecs429.text.stemmer.SnowballStemmer;
import cecs429.text.stemmer.englishStemmer;

public class Stemmer {
    private static Stemmer thisStemmer = null;
    private SnowballStemmer stemmer;

    private Stemmer() {
        this.stemmer = new englishStemmer();
    }

    public static Stemmer getInstance() {
        if (thisStemmer == null)
            thisStemmer = new Stemmer();

        return thisStemmer;
    }

    public String stemToken(String token) {
        stemmer.setCurrent(token);
        stemmer.stem();
        return stemmer.getCurrent();
    }
}
