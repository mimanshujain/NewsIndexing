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
	//^[A-Z][a-zA-Z0-9-,]?
	//[A-Z]+
	private static String firstCapital; 			//="[A-Z]{1}(.*)";
	private static String allCapital	;			//="[A-Z\\,\\-0-9]+";
	private static String allCapitalWithDots;		//="[A-Z\\,\\-0-9\\.]+";

	private Pattern checkFirstCapital; 
	private Pattern checkAllCapital; 
	private Pattern checkCapitalWithDots; 
	
	private Matcher matchFirstCapital=null;
	private Matcher matchAllCapital=null;
	private Matcher matchCapitalWithDots=null;
	//	private nextValue;
	//	private static final String ="[A-Z.]+";

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
				Token tk=tStream.next();

				if(tk==null) return true;

				String tempToken = tk.getTermText();
				String transitionalString=tempToken;
				String previousTokenString="";
				Token token;

				if (tempToken!=null && !"".equals(tempToken)) {

					matchFirstCapital=checkFirstCapital.matcher(tempToken);
					matchCapitalWithDots=checkCapitalWithDots.matcher(tempToken);
					matchAllCapital=checkAllCapital.matcher(tempToken);

					if(matchFirstCapital.matches() )//&& !matchAllCapital.matches() && !matchCapitalWithDots.matches()
						//if(tempToken.matches(firstCapital) && !tempToken.matches(allCapital) && !tempToken.matches(allCapitalWithDots))
					{		
						if(tempToken.length()>1)
						{
							//Getting the Previous Token
							token=tStream.getPrevious(-2);
							//Check if previous token exists and if it, then check if it was the last word of previous Line.
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

							//This is when the current token is the very first word in the Stream.
							if(tStream.isFirst())
							{
								tempToken=transitionalString.toLowerCase();
								tk.setTermText(tempToken);
								//return tStream.hasNext();
							}
							else
							{
								transitionalString=givePreviousUpper(transitionalString, previousTokenString);
								//tempToken=transitionalString.toLowerCase();
								tk.setTermText(transitionalString);
								//return tStream.hasNext();
							}
						}
						else
						{
							//tempToken=tempToken.toLowerCase();
							tempToken=transitionalString.toLowerCase();
							tk.setTermText(tempToken);
						}
						//return tStream.hasNext();
					}
					else if(matchAllCapital.matches() || matchCapitalWithDots.matches())
						//					else if(tempToken.matches(allCapital) || tempToken.matches(allCapitalWithDots))
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

						//return tStream.hasNext();
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
		return false;
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
		//		}
		//		else
		//			return currentValue;

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

	//	private String giveNextUpperString(String currentValue,int i)
	//	{
	//		if(tStream.hasNext(i))
	//		{
	//			Token token=tStream.getNext(i);
	//			if(token!=null)
	//			{
	//				String nextTokenString=token.getTermText();
	//				if(nextTokenString.matches(firstCapital) && !nextTokenString.matches(allCapital) && !nextTokenString.matches(allCapitalWithDots))
	//				{
	//					String output=currentValue.substring(0,1).toUpperCase()+currentValue.substring(1);
	//					currentValue=output+ " "+nextTokenString;
	//					//tk.setTermText(currentValue);
	//					token=tStream.next();
	//					if(token.getTermText().equals(nextTokenString))
	//						tStream.remove();
	//					return currentValue;
	//				}
	//			}
	//			else
	//				return null;
	//		}
	//		else
	//			return null;
	//
	//		return currentValue;
	//	}

	//	private boolean isNextUpper()
	//	{
	//		Token token=tStream.getNext(tStream.getIndex());
	//		if(token!=null)
	//		{
	//			String nextTokenString=token.getTermText();
	//			if(nextTokenString.matches(firstCapital) && !nextTokenString.matches(allCapital) && !nextTokenString.matches(allCapitalWithDots))
	//			{
	//				return true;
	//			}
	//		}
	//		return false;
	//	}
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#getStream()
	 */
	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return tStream;
	}

}

