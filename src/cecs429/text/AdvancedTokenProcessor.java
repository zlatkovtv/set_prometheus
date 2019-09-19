package cecs429.text;

import java.util.List;

/**
 * A BasicTokenProcessor creates terms from tokens by removing all non-alphanumeric characters from the token, and
 * converting it to all lowercase.
 */
public class AdvancedTokenProcessor implements TokenProcessor {
	@Override
	public List<String> processToken(String token) {
		return null; ///token.replaceAll("\\W", "").toLowerCase();
	}

	public List<String> removeAlphaNum(){return null;}

	public List<String> removeApost(){return null;}

	public List<String> hyphenate(){return null;}

	public List<String> toLowerCase(){return null;}

	public List<String> callStemmer(){return null;}
}
