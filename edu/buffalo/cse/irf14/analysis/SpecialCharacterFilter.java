package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecialCharacterFilter extends TokenFilter {

	public SpecialCharacterFilter(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	// Jagvir
//	private static final String splChrFilterMinusMathSymbol = "[a-zA-Z0-9\\^\\+\\*\\-]+";
	private static final String splChrFilterMinusMathSymbol = "[a-zA-Z0-9]*[\\^\\+\\*\\-]+[a-zA-Z0-9]*";
//	private static final String splChrMathOpr = "[@<|>\\/\\*\\+\\^\\=\\&[\\/\\_\\\\]+]+";
	private static final String splChrMathOpr = "[@<|>\\/\\*\\+\\^=:;&_\\\\]";
	private static final String splChrAlphaAlpha1 = 
			"[(\\-)*([a-zA-Z\\+\\!\\#\\$\\%\\^\\&\\*\\()])+(\\-)*([a-zA-Z\\+\\!\\#\\$\\%\\^\\&\\*\\(\\)])+(\\-)*]+";
	private static final String splChrAlphaAlpha = 
			"([\\+\\!#$%\\^&\\*\\(\\)~])";
	private static final String splChrAtRate="([a-zA-Z0-9]*)(@)([a-zA-Z0-9.]*)";
	public Pattern checkSplCharacter = null;
	public Matcher matchSplCharacter = null;
	private String stringToSaveTemp = "";
	private String stringToSaveTemp2 = "";

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub

		try {
			if (tStream.hasNext()) {
				
				Token tk = tStream.next();

				if (tk.getTermText() != "" || tk.getTermText() != null) {
					// Jagvir FInding all symbols save math operations
					
					String tempToken = tk.getTermText();
					checkSplCharacter = Pattern
							.compile(splChrFilterMinusMathSymbol);
					matchSplCharacter = checkSplCharacter.matcher(tempToken
							.trim());
					String temp = "";
					int i = 0;
					while (matchSplCharacter.find()) {
						temp += matchSplCharacter.group();
						
					}
					if (!temp.isEmpty())
						tempToken = temp;

					// 21stSept math symbol code

					if (!tempToken.trim().equals("") || tempToken != "") {
						
						stringToSaveTemp = tempToken.toString();
						
						stringToSaveTemp = stringToSaveTemp.replaceAll(
								splChrMathOpr, "");
						
						stringToSaveTemp = stringToSaveTemp.replaceAll(
								splChrAlphaAlpha, "");
						
						tempToken = stringToSaveTemp.trim();

						// splCharacters
						if (tempToken.matches(splChrAlphaAlpha)) {
							
							checkSplCharacter = Pattern
									.compile(splChrAlphaAlpha);
							matchSplCharacter = checkSplCharacter
									.matcher(tempToken.trim());
							if (matchSplCharacter.find()) {
								stringToSaveTemp2 = tempToken.toString();

								stringToSaveTemp = stringToSaveTemp.replaceAll(
										"-", "");
								tempToken = stringToSaveTemp.trim();
								
							}

						}
						if (tempToken.matches(splChrAlphaAlpha1)) {
							
							checkSplCharacter = Pattern
									.compile(splChrAlphaAlpha1);
							matchSplCharacter = checkSplCharacter
									.matcher(tempToken.trim());
							if (matchSplCharacter.find()) {
								stringToSaveTemp2 = tempToken.toString();

								stringToSaveTemp = stringToSaveTemp.replaceAll(
										"-", "");
								tempToken = stringToSaveTemp.trim();
								
							}

						}
//						if (matchSplCharacter.find()) {
//							if (matchSplCharacter.group(2) != null)
//								matchSplCharacter.replaceAll(matchSplCharacter.group(2));
//							
//							System.out.println();
//						}
						
						

					}

					tk.setTermText(tempToken.trim());
					// commit
					return true;
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
		return tStream;
	}

}
