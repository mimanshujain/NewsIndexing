package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecialCharacterFilter extends TokenFilter {

	public SpecialCharacterFilter(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	// Jagvir
	private static final String splCharacterFilterGeneric = "[^(a-zA-Z0-9)\\w+(a-zA-Z0-9)\\-\\s@&.]";
	private static final String splCharacter = "";
	public Pattern checkSplCharacter = null;
	public Matcher matchSplCharacter = null;

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub

		try {
			if (tStream.hasNext()) {
				Token tk = tStream.next();
				if (tk.getTermText() != null || tk.getTermText() != "") {
					String temp = tk.getTermText();

					checkSplCharacter = Pattern
							.compile(splCharacterFilterGeneric);
					matchSplCharacter = checkSplCharacter.matcher(temp.trim());

					if (matchSplCharacter.find()) {
						temp = matchSplCharacter.group();

					}

				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return null;
	}

}
