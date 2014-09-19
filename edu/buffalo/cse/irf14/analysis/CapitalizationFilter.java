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

	private static final String firstCapital="^[A-Z][a-zA-Z0-9-,]?";
	private static final String allCapital="[A-Z]+";
	private static final String allCapitalWithDots="[A-Z.]+";
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
				String tempToken = tk.getTermText();
				String transitionalString=tempToken;
				String nextTokenString="";
				Token token;
				if (tempToken!=null && !"".equals(tempToken)) {

					if(tempToken.matches(firstCapital) && !tempToken.matches(allCapital) && !tempToken.matches(allCapitalWithDots))
					{		
						if(tempToken.length()>1)
						{
							token=(Token)tStream.getPrevious();
							if(token!=null && (token.getTermText().contains(".")))
							{
								transitionalString=transitionalString.toLowerCase();
							}
							else if(tStream.isFirst())
							{
								transitionalString=transitionalString.toLowerCase();
							}

							transitionalString=giveNextUpperString(tempToken,0);
							if(transitionalString!=null)
								transitionalString=giveNextUpperString(transitionalString,1);
							else
								transitionalString=tempToken;
						}
						else
						{
							tempToken=tempToken.toLowerCase();
							return true;
						}
					}
					else if(tempToken.matches(allCapital) && !tempToken.matches(allCapitalWithDots))
					{
						List<String> lst=tStream.getWords();
						if(lst.size()>0)
						{
							for(String str : lst)
							{
								if(!str.matches(allCapital) && !str.matches(allCapitalWithDots))
								{
									break;
								}
							}
						}
					}

				}
				return true;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}

	private String giveNextUpperString(String currentValue,int i)
	{
		if(tStream.hasNext())
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
		return null;
	}

	public static void main(String[] args) throws TokenizerException {
		String ip="this. My name is Mimanshu and I am a good boy. So what, get lost.";
		Tokenizer tz=new Tokenizer();
		TokenStream ts=tz.consume(ip);
		CapitalizationFilter cp = new CapitalizationFilter(ts);
		while(cp.increment()){ }
	}
}

