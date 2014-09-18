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
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#increment()
	 */
	@Override
	public boolean increment() throws TokenizerException {
		try
		{			
			if(tStream.hasNext())
			{
				Token tk=tStream.next();
				List<String> lst=tStream.getWords();
				if(lst.indexOf(tk.getTermText())==0)
				{
					System.out.println("Only element in list");
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

