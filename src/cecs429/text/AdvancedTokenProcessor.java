package cecs429.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A BasicTokenProcessor creates terms from tokens by removing all non-alphanumeric characters from the token, and
 * converting it to all lowercase.
 */
public class AdvancedTokenProcessor implements TokenProcessor {
    private Stemmer stemmer;

    public AdvancedTokenProcessor() {
        stemmer = Stemmer.getInstance();
    }

    @Override
    public List<String> processToken(String token) {
        List<String> processedTokens;
        List<String> stemmedTokens = new ArrayList<>();

        String processedToken = normalizeToken(token);
        processedTokens = hyphenate(processedToken);

        for (String processToken : processedTokens) {
            stemmedTokens.add(stemmer.stemToken(processToken));
        }

        return stemmedTokens;
    }

    public String processQueryToken(String token) {
        return stemmer.stemToken(normalizeToken(token));
    }

    public String normalizeToken(String token) {
        String processString = removeAlphaNum(token);
        processString = removeApost(processString);
        return toLowerCase(processString);
    }

    private String removeAlphaNum(String s) {

        StringBuilder sb = new StringBuilder(s);

        while (sb.length() > 0 && !Character.isDigit(sb.charAt(0)) && !Character.isLetter(sb.charAt(0))) {

            sb.deleteCharAt(0);

        }

        while (sb.length() > 0 && !Character.isDigit(sb.charAt(sb.length() - 1)) && !Character.isLetter(sb.charAt(sb.length() - 1))) {

            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    private String removeApost(String string) {
        string = string.replaceAll("'", "");
        string = string.replaceAll("\"", "");
        return string;
    }

    private String toLowerCase(String string) {
        string = string.toLowerCase();
        return string;
    }

    private List<String> hyphenate(String string) {
        ArrayList<String> sb = new ArrayList<>();
        sb.add(string.replaceAll("-", ""));
        if (sb.indexOf('-') > 0) {
            sb.addAll(Arrays.asList(string.split("-")));
        }

        return sb;
    }
}
