package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StemmerFilter extends TokenFilter {

	private static final int INC = 50;
	/* unit of size whereby b is increased */
	public StemmerFilter(TokenStream stream) {
		super(stream);

	}

	private Pattern checkSymbol = null;
	private Matcher matchSymbol = null;

	@Override
	public boolean increment() throws TokenizerException {

		//tStream.reset();
		try
		{
			if (tStream.hasNext()) 
			{
				Token tk=tStream.next();
				String tempToken = tk.getTermText();
				if (tempToken!=null && !"".equals(tempToken)) {
					if(tempToken.matches("[a-zA-Z]+"))
					{
						Stemmer st=new Stemmer();
						st.add(tempToken.toCharArray(), tempToken.toCharArray().length);
						st.stem();
						tempToken=st.toString();
						tk.setTermText(tempToken);
						return tStream.hasNext();
					}

				}
			}
			else
				return tStream.hasNext();
		}

		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return tStream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return tStream;
	}
}
