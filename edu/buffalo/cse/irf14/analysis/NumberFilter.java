package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberFilter extends TokenFilter {

	private static String numberRegPureNumber;// = "[[\\d]+[\\.\\,]]+";
	private static String numberRegAlphaRemoval;// = "[^a-zA-Z]+";
	static
	{
		numberRegPureNumber = "[[\\d]+[\\.\\,]]+";
		numberRegAlphaRemoval = "[^a-zA-Z]+";
	}
	//	private Pattern checkNumber = null;
	//	private Pattern checkNumPure=null;
	private Pattern checkAlpha=null;
	private Pattern checkPure=null;
	
	public Matcher matchNumber = null;
	public String stringToSave = null;



	public NumberFilter(TokenStream stream) {
		super(stream);
		checkAlpha=Pattern.compile(numberRegAlphaRemoval);
		checkPure=Pattern.compile(numberRegPureNumber);
	}

	@Override
	public boolean increment() throws TokenizerException {
		try {
			if (tStream.hasNext()) {
				Token tk = tStream.next();
				{
					if(tk!=null  && !tk.isTime() && !tk.isDate())
					{
						String tempToken = tk.getTermText();
						if (!tempToken.equals(null) && !tempToken.isEmpty() && !tempToken.matches("[a-zA-Z]+")) {
								matchNumber = checkAlpha.matcher(tempToken);
								// System.out.println(tempToken);
								if (matchNumber.matches()) {
									//System.out.println(tempToken);
									tempToken = tempToken.replaceAll(
											numberRegPureNumber, "");
								}
								
								else if((matchNumber = checkPure.matcher(tempToken)).matches())
								{
									tempToken = tempToken.replaceAll(
											numberRegPureNumber, "");
								}
								
								if("".equals(tempToken.trim())) {
									tStream.remove();
								}
								else {
									tk.setTermText(tempToken.trim());
								}
						}
						return tStream.hasNext();
					}
				}
			} 
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new TokenizerException();
		}
		return tStream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return tStream;
	}

}
