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

	private static final String removePunc = "(.*)(\\b)";
	private static final String expandApos = "(.*)(\\'.*)";
	private static final String alphaAlpha = "^([A-Za-z]+)([-])([A-Za-z]+)$";
	private static final String alphaNum = "([a-zA-Z]+)(\\-)([0-9]+)";
	private static final String numAlpha = "([0-9]+)(\\-)([a-zA-Z]+)";
	private static final String expandWithN = "(.*)(n)(\\'.*)";
	private static final String doubleAposwithN = "(.*)(n)(\\'.*)(\\'.*)";
	private static final String doubleAposwithD = "(.*)(\\'d*)(\\'.*)";

	public Pattern checkSymbol = null;
	public Matcher matchSymbol = null;

	@Override
	public boolean increment() throws TokenizerException {

		tStream.reset();
		try
		{
			while (tStream.hasNext()) {
				Token tk = tStream.next();

				if (tk.getTermText() != "" || tk.getTermText() != null) {
					String tempToken = tk.getTermText();
					String contractionWord = "";

					checkSymbol = Pattern.compile(removePunc);
					matchSymbol = checkSymbol.matcher(tempToken.trim());

					if (matchSymbol.groupCount() > 0) {
						tempToken = matchSymbol.group(1);
					}

					boolean smallWordMatch = false;
					int i = 0;
					for (String[] str : chameleonSmallWords) {
						if (tempToken.equals(str[0])) {
							smallWordMatch = true;
							tempToken = chameleonSmallWords[i][0];
						}
						i++;
					}
					boolean bigWordMatch = false;
					for (String[] str : chameleonBigWords) {
						if (tempToken.equals(str[0])) {
							bigWordMatch = true;
							tempToken = chameleonSmallWords[i][0];
						}
						i++;
					}
					if (!smallWordMatch || !bigWordMatch) {
						checkSymbol = Pattern.compile(expandWithN);
						matchSymbol = checkSymbol.matcher(tempToken.trim());

						if (matchSymbol.find()) {
							tempToken = matchSymbol.group(1);
							contractionWord = matchSymbol.group(3);
							tempToken = (tempToken + expandContractWord(contractionWord));
						} else if ((matchSymbol = (checkSymbol = Pattern
								.compile(expandApos)).matcher(tempToken.trim()))
								.find()) {
							checkSymbol = Pattern.compile(expandApos);
							matchSymbol = checkSymbol.matcher(tempToken.trim());

							if (matchSymbol.find()) {
								tempToken = matchSymbol.group(1);
								contractionWord = matchSymbol.group(2);
								tempToken = (tempToken + expandContractWord(contractionWord))
										.trim();
							}
						} else if ((matchSymbol = (checkSymbol = Pattern
								.compile(doubleAposwithN))
								.matcher(tempToken.trim())).find()) {
							String extraContration;
							tempToken = matchSymbol.group(1);
							contractionWord = matchSymbol.group(2);
							extraContration = matchSymbol.group(4);
							tempToken = (tempToken
									+ expandContractWord(contractionWord) + expandContractWord(extraContration))
									.trim();
						} else if ((matchSymbol = (checkSymbol = Pattern
								.compile(doubleAposwithD))
								.matcher(tempToken.trim())).find()) {
							String extraContration;
							tempToken = matchSymbol.group(1);
							contractionWord = matchSymbol.group(2);
							extraContration = matchSymbol.group(4);
							tempToken = (tempToken
									+ expandContractWord(contractionWord) + expandContractWord(extraContration))
									.trim();
						}
					}

					checkSymbol = Pattern.compile(alphaAlpha);
					matchSymbol = checkSymbol.matcher(tempToken.trim());

					if (!matchSymbol.find()) {
						tempToken = matchSymbol.group(1) + matchSymbol.group(3);
					}
					tk.setTermText(tempToken);
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
		/*
		 * switch (conWord) { case "'s": return ""; case "'t": return "not";
		 * case "'ll": return "will"; case "'ts": return "not"; default: return
		 * conWord; }
		 */
		if (conWord == "'s")
			return "";
		else if (conWord == "'t")
			return "not";
		else if (conWord == "'ll")
			return "will";
		else if (conWord == "'ts")
			return "not";
		else if (conWord == "'re")
			return "are";
		else if (conWord == "'m")
			return "am";
		else if (conWord == "'d")
			return "would";
		else if (conWord == "'em")
			return "them";
		else
			return conWord;

	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * public static void main(String[] args) { SymbolFilter sm = new
	 * SymbolFilter(null); String ip,op; ip="shouldn't"; op=sm.test(ip);
	 * System.out.println(op); }
	 * 
	 * public String test(String tk){
	 * 
	 * // if (tk.getTermText() != "" || tk.getTermText() != null) { String
	 * tempToken = tk; String contractionWord = ""; checkSymbol =
	 * Pattern.compile(removePunc); matchSymbol =
	 * checkSymbol.matcher(tempToken.trim()); if (matchSymbol.groupCount() > 0)
	 * { tempToken = matchSymbol.group(1); } checkSymbol =
	 * Pattern.compile(expandApos); matchSymbol =
	 * checkSymbol.matcher(tempToken.trim()); // change this condition // check
	 * for Exception if (matchSymbol.find()) { tempToken = matchSymbol.group(1);
	 * // tempToken=tempToken; contractionWord = matchSymbol.group(2); tempToken
	 * = (tempToken + expandContractWord(contractionWord)) .trim(); } return
	 * tempToken; // checkSymbol = Pattern.compile(alphaNum); // matchSymbol =
	 * checkSymbol.matcher(tempToken.trim()); // if (!matchSymbol.find()) { //
	 * // } // } }
	 */
}
