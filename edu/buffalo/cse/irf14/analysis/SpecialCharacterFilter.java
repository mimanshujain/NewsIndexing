package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecialCharacterFilter extends TokenFilter {
	
	public SpecialCharacterFilter(TokenStream stream) {
		super(stream);

		checkSplChrMinusMathSym=Pattern.compile(splChrFilterMinusMathSymbol);
		checkSplChrAlphaAlpha1=Pattern.compile(splChrAlphaAlpha1);
		checkSplChrAlphaAlpha=Pattern.compile(splChrAlphaAlpha);
		checkSpl=Pattern.compile("\\w+");
	}

	private static String splChrFilterMinusMathSymbol;// = "[a-zA-Z0-9]*[\\^\\+\\*\\-]+[a-zA-Z0-9]*";
	//	private  String splChrMathOpr = "[@<|>\\/\\*\\+\\^\\=\\&[\\/\\_\\\\]+]+";
	private static String splChrMathOpr;// = "[\"@<|>\\/\\*\\+\\^=:;&_\\\\]";
	private static String splChrAlphaAlpha1;// = 
	//			"[(\\-)*([a-zA-Z\\+\\!\\#\\$\\%\\^\\&\\*\\()])+(\\-)*([a-zA-Z\\+\\!\\#\\$\\%\\^\\&\\*\\(\\)])+(\\-)*]+";
	private static String splChrAlphaAlpha;// = 
	//			"([\\+\\!#$%\\^&\\*\\(\\)~])";
	//	private  String splChrAtRate;//="([a-zA-Z0-9]*)(@)([a-zA-Z0-9.]*)";
	private static String stringToSaveTemp = "";
	private static String stringToSaveTemp2 = "";
	
	static 
	{
		splChrFilterMinusMathSymbol = "[a-zA-Z0-9]*[\\^\\+\\*\\-]+[a-zA-Z0-9]*";
		splChrMathOpr = "[@<|>\\/\\*\\+\\^=:;&_\\\\]";
		splChrAlphaAlpha1 = 
				"[(\\-)*([a-zA-Z\\+\\!\\#\\$\\%\\^\\&\\*\\()])+(\\-)*([a-zA-Z\\+\\!\\#\\$\\%\\^\\&\\*\\(\\)])+(\\-)*]+";
		splChrAlphaAlpha = 
				"([\\+\\!#$%\\^&\\*\\(\\)~])";
	}
	
	//	private Pattern checkSplCharacter = null;
	private Pattern checkSplChrMinusMathSym=null;
	//	private Pattern checkSplChrChrMathOpr=null;
	private Pattern checkSplChrAlphaAlpha1=null;
	private Pattern checkSplChrAlphaAlpha=null;
	//	private Pattern checkSplAtRate=null;
private Pattern checkSpl=null;

	private Matcher matchSplCharacter = null;

	@Override
	public boolean increment() throws TokenizerException {
		try {
			if (tStream.hasNext()) {

				Token tk = tStream.next();
				if(tk!=null)
				{
					String tempToken = tk.getTermText();

					if (!tempToken.equals(null) && !tempToken.equals("") && !tempToken.matches("[a-zA-Z0-9]*")) {
						matchSplCharacter = checkSplChrMinusMathSym.matcher(tempToken.trim());
						String temp = "";
						while (matchSplCharacter.find()) {
							temp += matchSplCharacter.group();

						}
						if (!temp.isEmpty())
							tempToken = temp;						

						if (!tempToken.equals(null) && !tempToken.trim().equals("")) {

							stringToSaveTemp = tempToken.toString();

							stringToSaveTemp = stringToSaveTemp.replaceAll(
									splChrMathOpr, "");

							stringToSaveTemp = stringToSaveTemp.replaceAll(
									splChrAlphaAlpha, "");

							tempToken = stringToSaveTemp.trim();

							if (tempToken.matches(splChrAlphaAlpha)) {
						
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
						if(tempToken.equals("") || tempToken.matches("[?.,\\\"-%\\d!_]"))
						{
							tStream.remove();
						}
						else {
							if(tempToken.contains("\""))
								tk.setTermText(tempToken.replaceAll("\"", "").trim());
							else
								tk.setTermText(tempToken.trim());
						}
						return tStream.hasNext();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new TokenizerException();
		}

		return tStream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return tStream;
	}

}
