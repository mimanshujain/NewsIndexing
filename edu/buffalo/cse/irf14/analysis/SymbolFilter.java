	//Few words are taken from Wikipedia

package edu.buffalo.cse.irf14.analysis;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author SherlockED
 *
 */
public class SymbolFilter extends TokenFilter {

	public SymbolFilter(TokenStream stream) {
		super(stream);

		checkPunc=Pattern.compile(removePunc);
		checkExpandApos=Pattern.compile(expandApos);
		checkAlphaAlpha=Pattern.compile(alphaAlpha);
		checkExpandWithN=Pattern.compile(expandWithN);
		checkDoubleN=Pattern.compile(doubleAposwithN);
		checkDoubleD=Pattern.compile(doubleAposwithD);
		checkOnlySpl=Pattern.compile(onlySpecial);
		checkDoubSpl=Pattern.compile(doubleSpecial);
		checkBasic=Pattern.compile("\\W+");
	}

	String[][] chameleonSmallWords = { { "ain't", "am not" }, { "can't", "cannot" },
			{ "won't", "will not" }, { "how's", "hows is" },
			{ "it's", "it is" }, { "shan't", "shall not" },
			{ "let's", "let us" }, { "what's", "what is" },
			{ "there's", "there is" }, { "where's", "where is" },
			{ "when's", "when is" }, { "who's", "who is" },
			{ "why's", "why is" }, { "ma'am", "madam" },
			{ "y'all", "you all" }, { "y'all'd've", "you all should have" } };

	String[][] chameleonBigWords = { { "Ain't", "Am not" }, { "Can't", "Cannot" },
			{ "Won't", "Will not" }, { "How's", "Hows is" },
			{ "It's", "It is" }, { "Shan't", "Shall not" },
			{ "Let's", "Let us" }, { "What's", "What is" },
			{ "There's", "There is" }, { "Where's", "Where is" },
			{ "When's", "When is" }, { "Who's", "Who is" },
			{ "why's", "why is" }, { "ma'am", "madam" },
			{ "Y'all", "You all" }, { "Y'all'd've", "You all should have" } };

	//	private static final String removePunc = "(.*)(\\b)(.*)";
	private static  String removePunc ;//= "(.*[^!?.]+)(.*)";
	private static  String expandApos;// = "(.*)(\\'.*)";
	private static  String alphaAlpha;// = "^([A-Za-z]+)([-]+)([A-Za-z]+)$";
	private static  String alphaNum;// = "([a-zA-Z]+)(\\-)([0-9]+)";
	private static  String numAlpha;// = "([0-9]+)(\\-)([a-zA-Z]+)";
	private static  String expandWithN;// = "(.*)(n)(\\'.*)";
	private static  String doubleAposwithN;// = "(.*)(n)(\\'.*)(\\'.*)";
	private static  String doubleAposwithD;// = "(.*)(\\'d*)(\\'.*)";
	private static  String onlySpecial;//="([a-zA-Z0-9]+)";
	private  static String doubleSpecial;

	static
	{
		removePunc = "(.*[^!?.,\"]+)(.*)";
		expandApos = "(.*)(\\'.*)";
		alphaAlpha = "^([A-Za-z]+)([-]+)([A-Za-z]+)$";
		expandWithN = "(.*)(n)(\\'[t])";
		doubleAposwithN = "(.*)(n)(\\'.*)(\\'.*)";
		doubleAposwithD = "(.*)(\\'d*)(\\'.*)";
		onlySpecial="(^[-]+)(.*)";
		doubleSpecial= "(.*[^-]+)([-]+$)";
	}
	String contractionWord = "";

//	private Pattern checkSymbol = null;
	private Pattern checkPunc=null;
	private Pattern checkExpandApos=null;
	private Pattern checkAlphaAlpha=null;
	private Pattern checkExpandWithN=null;
	private Pattern checkDoubleN=null;
	private Pattern checkDoubleD=null;
	private Pattern checkOnlySpl=null;
	private Pattern checkDoubSpl=null;
	private Pattern checkBasic=null;
	
	private Matcher matchSymbol = null;

	@Override
	public boolean increment() throws TokenizerException {
		try
		{
			if (tStream.hasNext()) {
				Token tk = tStream.next();
				if(tk!=null)
				{
					String tempToken = tk.getTermText();	
					if (!tempToken.equals(null) && !tk.getTermText().isEmpty()) {	

						//checkSymbol = Pattern.compile(removePunc);
						matchSymbol = checkPunc.matcher(tempToken.trim());
						//1					
						if (matchSymbol.matches()) 
						{
							tempToken = matchSymbol.group(1);
						}

						String[] strArray;
						strArray=tempToken.split(" ");
						StringBuilder sb=new StringBuilder();
						for(String str: strArray)
						{
							sb.append(getMeFilterString(str)+" ");
						}

						tempToken=sb.toString().trim();
						tk.setTermText(tempToken);

						return tStream.hasNext();
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}

	private String getMeFilterString(String tempToken)
	{
		//2
		boolean wordMatch = false;
		int i = 0;

		if(tempToken.contains("'"))
		{
			for (String[] str : chameleonSmallWords) {
				if (tempToken.equals(str[0])) {
					wordMatch = true;
					tempToken = chameleonSmallWords[i][1];
					break;
				}
				i++;
			}
			i=0;
			if(!wordMatch)
			{
				for (String[] str : chameleonBigWords) {
					if (tempToken.equals(str[0])) {
						wordMatch = true;
						tempToken = chameleonBigWords[i][1];
						break;
					}
					i++;
				}
			}
		}

		//3					
		if (!wordMatch) {
			//checkSymbol = Pattern.compile(expandWithN);
			matchSymbol = checkExpandWithN.matcher(tempToken.trim());
			//3-1
			if (matchSymbol.matches()) 
			{
				tempToken = matchSymbol.group(1);
				contractionWord = matchSymbol.group(3);
				String str=expandContractWord(contractionWord);
				if(str!=null)
					tempToken = (tempToken + " "+expandContractWord(contractionWord));
				else
					tempToken=tempToken+contractionWord;
			}
			//3-2			
			else if ((matchSymbol = checkExpandApos.matcher(tempToken.trim()))
					.matches()) 
			{
				tempToken = matchSymbol.group(1);
				contractionWord = matchSymbol.group(2);
				String str=expandContractWord(contractionWord);

				if(str!=null && !str.equals(""))
					tempToken = (tempToken + " "+ str)
					.trim();

				else if(!tempToken.equals("")&&tempToken.contains("'"))
					tempToken=(tempToken+contractionWord).replaceAll("[']", "");

				else if(str==null && !contractionWord.equals("") && !tempToken.equals(""))
				{
					tempToken=(tempToken+contractionWord).replaceAll("[']", "");
				}

				else if(!contractionWord.equals("")&&tempToken.equals("")&&contractionWord.contains("'"))
					tempToken=contractionWord.replaceAll("[']", "");

			} 
			//3-3
			else if ((matchSymbol = checkDoubleN.matcher(tempToken.trim())).matches()) 
			{
				String extraContration;
				tempToken = matchSymbol.group(1);
				contractionWord = matchSymbol.group(2);
				extraContration = matchSymbol.group(4);
				tempToken = (tempToken+" "
						+ expandContractWord(contractionWord)+ " " + expandContractWord(extraContration))
						.trim();
			} 
			//3-4
			else if ((matchSymbol = checkDoubleD.matcher(tempToken.trim())).matches()) 
			{
				String extraContration;
				tempToken = matchSymbol.group(1);
				contractionWord = matchSymbol.group(2);
				extraContration = matchSymbol.group(4);
				tempToken = (tempToken +" "
						+ expandContractWord(contractionWord) +" "+ expandContractWord(extraContration))
						.trim();
			}
		}
		//4
		//checkSymbol = Pattern.compile(alphaAlpha);
		matchSymbol = checkAlphaAlpha.matcher(tempToken.trim());

		if (matchSymbol.matches()) {
			tempToken = matchSymbol.group(1) +" "+ matchSymbol.group(3);						
		}
		//5 check for token with only special char
		//checkSymbol = Pattern.compile("(^[-]+)(.*)");
		matchSymbol = checkOnlySpl.matcher(tempToken.trim());

		if(matchSymbol.matches())
		{
			tempToken=matchSymbol.group(2).trim();
			if("".equals(tempToken))
			{
				tStream.remove();
				//return true;
			}			
		}
		//6					
		//checkSymbol=Pattern.compile("(.*[^-]+)([-]+$)");
		matchSymbol=checkDoubSpl.matcher(tempToken.trim());

		if(matchSymbol.matches())
		{
			tempToken=matchSymbol.group(1);
		}
		return tempToken;
	}


	private String expandContractWord(String conWord) {
		if (conWord.equals("'s"))
			return "";
		else if (conWord.equals("'t"))
			return "not";
		else if (conWord.equals("'ll"))
			return "will";
		else if (conWord.equals("'ts"))
			return "not";
		else if (conWord.equals("'re"))
			return "are";
		else if (conWord.equals("'m"))
			return "am";
		else if (conWord.equals("'d"))
			return "would";
		else if (conWord.equals("'em"))
			return "them";
		else if (conWord.equals("'ve"))
			return "have";
		else
			return null;

	}

	@Override
	public TokenStream getStream() {
		return tStream;
	}

}
