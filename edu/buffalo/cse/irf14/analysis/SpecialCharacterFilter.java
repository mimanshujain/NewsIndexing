package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecialCharacterFilter extends TokenFilter {

	public SpecialCharacterFilter(TokenStream stream) {
		super(stream);
		splChrFilterMinusMathSymbol = "[a-zA-Z0-9]*[\\^\\+\\*\\-]+[a-zA-Z0-9]*";
		splChrMathOpr = "[@<|>\\/\\*\\+\\^=:;&_\\\\]";
		splChrAlphaAlpha1 = 
				"[(\\-)*([a-zA-Z\\+\\!\\#\\$\\%\\^\\&\\*\\()])+(\\-)*([a-zA-Z\\+\\!\\#\\$\\%\\^\\&\\*\\(\\)])+(\\-)*]+";
		splChrAlphaAlpha = 
				"([\\+\\!#$%\\^&\\*\\(\\)~])";

		checkSplChrMinusMathSym=Pattern.compile(splChrFilterMinusMathSymbol);
		checkSplChrAlphaAlpha1=Pattern.compile(splChrAlphaAlpha1);
		checkSplChrAlphaAlpha=Pattern.compile(splChrAlphaAlpha);
	}

	// Jagvir
	//	private  String splChrFilterMinusMathSymbol = "[a-zA-Z0-9\\^\\+\\*\\-]+";
	private  String splChrFilterMinusMathSymbol;// = "[a-zA-Z0-9]*[\\^\\+\\*\\-]+[a-zA-Z0-9]*";
	//	private  String splChrMathOpr = "[@<|>\\/\\*\\+\\^\\=\\&[\\/\\_\\\\]+]+";
	private  String splChrMathOpr;// = "[@<|>\\/\\*\\+\\^=:;&_\\\\]";
	private  String splChrAlphaAlpha1;// = 
	//			"[(\\-)*([a-zA-Z\\+\\!\\#\\$\\%\\^\\&\\*\\()])+(\\-)*([a-zA-Z\\+\\!\\#\\$\\%\\^\\&\\*\\(\\)])+(\\-)*]+";
	private  String splChrAlphaAlpha;// = 
	//			"([\\+\\!#$%\\^&\\*\\(\\)~])";
	//	private  String splChrAtRate;//="([a-zA-Z0-9]*)(@)([a-zA-Z0-9.]*)";
	private String stringToSaveTemp = "";
	private String stringToSaveTemp2 = "";

	//	private Pattern checkSplCharacter = null;
	private Pattern checkSplChrMinusMathSym=null;
	//	private Pattern checkSplChrChrMathOpr=null;
	private Pattern checkSplChrAlphaAlpha1=null;
	private Pattern checkSplChrAlphaAlpha=null;
	//	private Pattern checkSplAtRate=null;


	private Matcher matchSplCharacter = null;

	@Override
	public boolean increment() throws TokenizerException {
		try {
			if (tStream.hasNext()) {

				Token tk = tStream.next();
				if(tk!=null)
				{
					String tempToken = tk.getTermText();
					if (!tempToken.equals(null) && !tempToken.equals("")) {
						// Jagvir FInding all symbols save math operations

						
						//					checkSplCharacter = Pattern
						//							.compile(splChrFilterMinusMathSymbol);
						matchSplCharacter = checkSplChrMinusMathSym.matcher(tempToken
								.trim());
						String temp = "";
						//					int i = 0;
						while (matchSplCharacter.find()) {
							temp += matchSplCharacter.group();

						}
						if (!temp.isEmpty())
							tempToken = temp;

						// 21stSept math symbol code

						if (!tempToken.equals(null) && !tempToken.trim().equals("")) {

							stringToSaveTemp = tempToken.toString();

							stringToSaveTemp = stringToSaveTemp.replaceAll(
									splChrMathOpr, "");

							stringToSaveTemp = stringToSaveTemp.replaceAll(
									splChrAlphaAlpha, "");

							tempToken = stringToSaveTemp.trim();

							// splCharacters
							if (tempToken.matches(splChrAlphaAlpha)) {

								//							checkSplCharacter = Pattern
								//									.compile(splChrAlphaAlpha);
								matchSplCharacter = checkSplChrAlphaAlpha
										.matcher(tempToken.trim());
								if (matchSplCharacter.find()) {
									stringToSaveTemp2 = tempToken.toString();

									stringToSaveTemp = stringToSaveTemp.replaceAll(
											"-", "");
									tempToken = stringToSaveTemp.trim();								
								}
							}
							if (tempToken.matches(splChrAlphaAlpha1)) {

								//							checkSplCharacter = Pattern
								//									.compile(splChrAlphaAlpha1);
								matchSplCharacter = checkSplChrAlphaAlpha1
										.matcher(tempToken.trim());
								if (matchSplCharacter.find()) {
									stringToSaveTemp2 = tempToken.toString();

									stringToSaveTemp = stringToSaveTemp.replaceAll(
											"-", "");
									tempToken = stringToSaveTemp.trim();			
								}

							}

						}

						tk.setTermText(tempToken.trim());
						// commit
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new TokenizerException();
		}

		return false;
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return tStream;
	}

}
