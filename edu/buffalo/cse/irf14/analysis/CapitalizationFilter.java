/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
	}
	//^[A-Z][a-zA-Z0-9-,]?
	//[A-Z]+
	private static final String firstCapital="[A-Z]{1}(.*)";
	private static final String allCapital="[A-Z\\,\\-0-9]+";
	private static final String allCapitalWithDots="[A-Z\\,\\-0-9\\.]+";
	//	private nextValue;
	//	private static final String ="[A-Z.]+";

	@Override
	public boolean increment() throws TokenizerException {
		try
		{			
			if(tStream.hasNext())
			{
				Token tk=tStream.next();
				//				List<String> lst=tStream.getWords();
				//				if(lst.indexOf(tk.getTermText())==0)
				//				{
				//					System.out.println("Only element in list");
				//				}

				//Initializing some varibles to be used.
				String tempToken = tk.getTermText();
				String transitionalString=tempToken;
				String nextTokenString="";
				Token token;

				if (tempToken!=null && !"".equals(tempToken)) {

					if(tempToken.matches(firstCapital) && !tempToken.matches(allCapital) && !tempToken.matches(allCapitalWithDots))
					{		
						if(tempToken.length()>1)
						{
							//Geting the Previous Token
							token=(Token)tStream.getPrevious();
							//Check if previous token exists and if it, then check if it was the last word of previous Line.
							if(token!=null && (token.getTermText().contains(".")))
							{
								transitionalString=transitionalString.toLowerCase();
							}
							//This is when the current token is the very first word in the Stream.
							else if(tStream.isFirst())
							{
								transitionalString=transitionalString.toLowerCase();
							}

							//To check if the next token is also upper case.
							nextTokenString=giveNextUpperString(transitionalString,0);

							//To check if the second token from current is also upper case
							//Before that making sure that last one was not null and not same as input, coz we dont need to do anything in that case.
							if(nextTokenString!=null && !nextTokenString.equals(transitionalString))
							{
								transitionalString=nextTokenString;
								nextTokenString=giveNextUpperString(nextTokenString,1);
								//Doing the same as was doing with the last call.
								if(nextTokenString!=null && !nextTokenString.equals(transitionalString))
								{
									transitionalString=nextTokenString;
								}
							}
//							else
//							{
//								transitionalString=transitionalString.toLowerCase();
//							}
						}
						else
						{
							tempToken=tempToken.toLowerCase();

						}
						tempToken=transitionalString;
						//						tempToken=tempToken.toLowerCase();
						tk.setTermText(tempToken);
						return true;
					}
					else if(tempToken.matches(allCapital) || tempToken.matches(allCapitalWithDots))
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
//								while(int i<lst)
								int i=lst.size();
								
							}
						}
					}
					
//					else if(tempToken.matches(allCapitalWithDots))
//					{
//						List<String> lst=tStream.getWords();
//						if(lst.size()>0)
//						{
//							for(String str : lst)
//							{
//								if(!str.matches(allCapital) && !str.matches(allCapitalWithDots))
//								{
//									break;
//								}
//							}
//						}
//						
//					}
//					else
//					{
						//tempToken=tempToken.toLowerCase();
						tk.setTermText(tempToken);
						return true;
					

				}

				return true;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		//
		return false;
	}

	private String giveNextUpperString(String currentValue,int i)
	{
		if(tStream.hasNext(i))
		{
			Token token=tStream.getNext(i);
			if(token!=null)
			{
				String nextTokenString=token.getTermText();
				if(nextTokenString.matches(firstCapital) && !nextTokenString.matches(allCapital) && !nextTokenString.matches(allCapitalWithDots))
				{
					String output=currentValue.substring(0,1).toUpperCase()+currentValue.substring(1);
					currentValue=output+ " "+nextTokenString;
					//tk.setTermText(currentValue);
					token=tStream.next();
					if(token.getTermText().equals(nextTokenString))
						tStream.remove();
					return currentValue;
				}
			}
			else
				return null;
		}
		else
			return null;

		return currentValue;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#getStream()
	 */
	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return tStream;
	}

}

