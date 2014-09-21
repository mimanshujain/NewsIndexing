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
	}
	//Few words are taken from Wikipedia
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
	private static final String removePunc = "(.*[^!?.]+)(.*)";
	private static final String expandApos = "(.*)(\\'.*)";
	private static final String alphaAlpha = "^([A-Za-z]+)([-]+)([A-Za-z]+)$";
	private static final String alphaNum = "([a-zA-Z]+)(\\-)([0-9]+)";
	private static final String numAlpha = "([0-9]+)(\\-)([a-zA-Z]+)";
	private static final String expandWithN = "(.*)(n)(\\'.*)";
	private static final String doubleAposwithN = "(.*)(n)(\\'.*)(\\'.*)";
	private static final String doubleAposwithD = "(.*)(\\'d*)(\\'.*)";
	private static final String checkOnlySpecial="([a-zA-Z0-9]+)";
	private Pattern checkSymbol = null;
	private Matcher matchSymbol = null;

	@Override
	public boolean increment() throws TokenizerException {
		try
		{
			if (tStream.hasNext()) {
				Token tk = tStream.next();

				if (tk.getTermText() != "" || tk.getTermText() != null) {
					String tempToken = tk.getTermText();
					String contractionWord = "";
					checkSymbol = Pattern.compile(removePunc);
					matchSymbol = checkSymbol.matcher(tempToken.trim());
					if (matchSymbol.find()) 
					{
						tempToken = matchSymbol.group(1);
					}

					boolean wordMatch = false;
					int i = 0;
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
					if (!wordMatch) {

						checkSymbol = Pattern.compile(expandWithN);
						matchSymbol = checkSymbol.matcher(tempToken.trim());

						if (matchSymbol.find()) {
							tempToken = matchSymbol.group(1);
							contractionWord = matchSymbol.group(3);
							String str=expandContractWord(contractionWord);
							if(str!=null)
								tempToken = (tempToken + " "+expandContractWord(contractionWord));
							else
								tempToken=tempToken+contractionWord;
						} 
						else if ((matchSymbol = (checkSymbol = Pattern
								.compile(expandApos)).matcher(tempToken.trim()))
								.find()) 
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
						else if ((matchSymbol = (checkSymbol = Pattern
								.compile(doubleAposwithN))
								.matcher(tempToken.trim())).find()) 
						{
							String extraContration;
							tempToken = matchSymbol.group(1);
							contractionWord = matchSymbol.group(2);
							extraContration = matchSymbol.group(4);
							tempToken = (tempToken+" "
									+ expandContractWord(contractionWord)+ " " + expandContractWord(extraContration))
									.trim();
						} 
						else if ((matchSymbol = (checkSymbol = Pattern
								.compile(doubleAposwithD))
								.matcher(tempToken.trim())).find()) 
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

					checkSymbol = Pattern.compile(alphaAlpha);
					matchSymbol = checkSymbol.matcher(tempToken.trim());

					if (matchSymbol.find()) {
						tempToken = matchSymbol.group(1) +" "+ matchSymbol.group(3);
					}

					checkSymbol = Pattern.compile("(^[-]+)(.*)");
					matchSymbol = checkSymbol.matcher(tempToken.trim());

					if(matchSymbol.find())
					{
						tempToken=matchSymbol.group(2).trim();
						if("".equals(tempToken))
						{
							tStream.remove();
							return true;
						}
						
					}
					checkSymbol=Pattern.compile("(.*[^-]+)([-]+$)");
					matchSymbol=checkSymbol.matcher(tempToken.trim());
					
					if(matchSymbol.find())
					{
						tempToken=matchSymbol.group(1);
					}
					
					tk.setTermText(tempToken);

					return true;
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
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
