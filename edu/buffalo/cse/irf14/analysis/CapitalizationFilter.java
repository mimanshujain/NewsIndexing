/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author SherlockED
 *
 */
public class CapitalizationFilter extends TokenFilter {
	/**
	 * @param stream
	 */
	public CapitalizationFilter(TokenStream stream) {
		super(stream);

		checkFirstCapital=Pattern.compile(firstCapital);
		checkAllCapital=Pattern.compile(allCapital);
		checkCapitalWithDots=Pattern.compile(allCapitalWithDots);
	}

	private static String firstCapital; 			
	private static String allCapital	;			
	private static String allCapitalWithDots;	

	private Pattern checkFirstCapital; 
	private Pattern checkAllCapital; 
	private Pattern checkCapitalWithDots; 

	private Matcher matchFirstCapital=null;
	private Matcher matchAllCapital=null;
	private Matcher matchCapitalWithDots=null;


	static {
		firstCapital="^[A-Z][^A-Z]+";
		allCapital="[A-Z\\,\\-0-9\\'\\<\\>]+";
		allCapitalWithDots="[A-Z\\,\\-0-9\\.\\'\\<\\>]+";
	}

	@Override
	public boolean increment() throws TokenizerException {
		try
		{			
			if(tStream.hasNext())
			{
				if(tStream.sizeOfStream() == 1) return false;
				
				Token tk=tStream.next();

				if(tk==null) return tStream.hasNext();

				String tempToken = tk.getTermText();
				String transitionalString=tempToken;
				String previousTokenString="";
				Token token;
				
				if (tempToken!=null && !"".equals(tempToken) && !tempToken.equals(tempToken.toLowerCase())) {

					matchFirstCapital=checkFirstCapital.matcher(tempToken);
					matchCapitalWithDots=checkCapitalWithDots.matcher(tempToken);
					matchAllCapital=checkAllCapital.matcher(tempToken);

					if(matchFirstCapital.matches())
					{		
						if(tempToken.length()>1)
						{
							token=tStream.getPrevious(-2);

							if(token!=null)
							{
								previousTokenString=token.getTermText();
								String prevWord=previousTokenString;
								prevWord=prevWord.substring(prevWord.length()-1, prevWord.length());
								int count = previousTokenString.length() - previousTokenString.replace(".", "").length();
								if(count==1)
								{
									tempToken=transitionalString.toLowerCase();
									tk.setTermText(tempToken);
									return tStream.hasNext();
								}
							}

							if(tStream.isFirst())
							{
								tempToken=transitionalString.toLowerCase();
								tk.setTermText(tempToken);

							}
							else
							{
								transitionalString=givePreviousUpper(transitionalString, previousTokenString);
								tk.setTermText(transitionalString);
							}
						}
						else
						{
							tempToken=transitionalString.toLowerCase();
							tk.setTermText(tempToken);
						}
					}
					else if(matchAllCapital.matches() || matchCapitalWithDots.matches())
					{
						if(tempToken.length()>1)
						{
							List<String> lst=tStream.getWords();
							if(lst.size()>0)
							{
								int flag=0;
								for(String str : lst)
								{
									if(!str.matches(allCapital) && !str.matches(allCapitalWithDots))
									{
										flag=1;
										break;
									}
								}
								if(flag==0)
								{
									transitionalString=transitionalString.toLowerCase();						
									tk.setTermText(transitionalString);
									doAllWordsLowerCase();
								}
							}
						}
						else
						{
							tempToken=tempToken.toLowerCase();
							tk.setTermText(tempToken);
						}

					}

					return tStream.hasNext();
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		//
		return tStream.hasNext();
	}


	private String givePreviousUpper(String currentValue,String termText){

		if(termText!=null && !"".equals(termText) && !termText.substring(termText.length()-1).equals(","))
		{
			if(termText.matches(firstCapital) && !termText.matches(allCapital) && !termText.matches(allCapitalWithDots))
			{
				currentValue=termText+" "+currentValue;
				tStream.remove(tStream.getIndex()-1);
				return currentValue;
			}
		}
		else
			return currentValue;

		return currentValue;
	}

	private void doAllWordsLowerCase()
	{
		while(tStream.hasNext())
		{
			Token tk=tStream.next();
			String tokenText="";
			if(tk!=null)
			{
				tokenText=tk.getTermText();
				tk.setTermText(tokenText.toLowerCase());
				int count = tokenText.length() - tokenText.replace(".", "").length();
				if(count==1 && !"".equals(tokenText))		
				{
					Token tk2 = tStream.getNext(tStream.getIndex());
					if(tk2!=null)
					{
						if(tk2.getTermText().matches("[A-Z]{1}(.*)"))
							break;
					}			
				}
			}
		}
	}
	@Override
	public TokenStream getStream() {
		return tStream;
	}

}

