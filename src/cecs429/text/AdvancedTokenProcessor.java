package cecs429.text;

import cecs429.text.stemmer.SnowballStemmer;
import cecs429.text.stemmer.englishStemmer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A BasicTokenProcessor creates terms from tokens by removing all non-alphanumeric characters from the token, and
 * converting it to all lowercase.
 */
public class AdvancedTokenProcessor implements TokenProcessor {
    private SnowballStemmer stemmer;

    public AdvancedTokenProcessor() {
        this.stemmer = new englishStemmer();
    }

    @Override
    public List<String> processToken(String token) {
        List<String> allStrings;
        String processString = token;

        processString = removeAlphaNum(processString);
        processString = removeApost(processString);
        processString = toLowerCase(processString);
        allStrings = hyphenate(processString);

        return callStemmer(allStrings);

    }

    public String removeAlphaNum(String s) {

        StringBuilder sb = new StringBuilder(s);
        int i = 0;
        while (!Character.isDigit(sb.charAt(i)) && !Character.isLetter(sb.charAt(i))) {
            sb.replace(i, i + 1, "");

        }
        i = sb.length() - 1;
        while (!Character.isDigit(sb.charAt(i)) && !Character.isLetter(sb.charAt(i))) {

            sb.deleteCharAt(i);
            i = sb.length() - 1;
        }

        return sb.toString();
    }

    public String removeApost(String s) {
        s = s.replaceAll("'", "");
        s = s.replaceAll("\"", "");
        return s;
    }


    public String toLowerCase(String s) {
        s = s.toLowerCase();
        return s;
    }

    public List<String> hyphenate(String s) {
        ArrayList<String> sb = new ArrayList<>();
        sb.add(s.replaceAll("-", ""));
        if(sb.indexOf('-') > 0) {
            sb.addAll(Arrays.asList(s.split("-")));
        }

        return sb;
    }

    public List<String> callStemmer(List<String> s) {
        List<String> stemmString = new ArrayList<String>();
        stemmString.addAll(s);

        for (String sw : s) {
            stemmer.setCurrent(sw);
            stemmer.stem();
            stemmString.add(stemmer.getCurrent());
        }

        return stemmString;
    }
}
