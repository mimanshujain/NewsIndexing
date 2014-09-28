/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nikhillo Class that converts a given string into a
 *         {@link TokenStream} instance
 */
public class Tokenizer {
	private String delimeterString;
	//public boolean isTitle=false;
	/**
	 * Default constructor. Assumes tokens are whitespace delimited
	 */
	public Tokenizer() {
		// TODO : YOU MUST IMPLEMENT THIS METHOD
		delimeterString = " ";
	}

	/**
	 * Overloaded constructor. Creates the tokenizer with the given delimiter
	 * 
	 * @param delim
	 *            : The delimiter to be used
	 */
	public Tokenizer(String delim) {
		// TODO : YOU MUST IMPLEMENT THIS METHOD
		delimeterString = delim;
	}
	public String docId;
	/**
	 * Method to convert the given string into a TokenStream instance. This must
	 * only break it into tokens and initialize the stream. No other processing
	 * must be performed. Also the number of tokens would be determined by the
	 * string and the delimiter. So if the string were "hello world" with a
	 * whitespace delimited tokenizer, you would get two tokens in the stream.
	 * But for the same text used with lets say "~" as a delimiter would return
	 * just one token in the stream.
	 * 
	 * @param str
	 *            : The string to be consumed
	 * @return : The converted TokenStream as defined above
	 * @throws TokenizerException
	 *             : In case any exception occurs during tokenization
	 */
	public TokenStream consume(String str) throws TokenizerException {
		try
		{
			if(str==null || str.equals("") )
				throw new TokenizerException();

			String singleSpaceStr;
			String[] strArray;
			TokenStream stream = new TokenStream();

			singleSpaceStr = str.replaceAll("\\s+", " ");
			//System.out.println(singleSpaceStr);
			strArray = singleSpaceStr.split(delimeterString);
			for (String tokenString : strArray) {
				Token token = new Token();
				token.setTermText(tokenString);
				//token.setTitle(this.isTitle);
				token.doc=docId;
				stream.setTokenStreamList(token);
			}
			//isTitle=false;
			return stream;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new TokenizerException();
		}
		//System.out.println(stream.next());
	}

	/* Jagvir Start */
	//	public static void main(String[] args) {
	//		Tokenizer tokenizer = new Tokenizer();
	//		try {
	//			tokenizer.consume("     This is a                 test String");
	//		} catch (TokenizerException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//
	//	}
	/* Jagvir Stop */
}
