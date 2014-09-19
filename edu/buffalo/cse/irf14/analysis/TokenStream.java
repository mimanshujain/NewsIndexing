//Jagvir
/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author nikhillo Class that represents a stream of Tokens. All
 *         {@link Analyzer} and {@link TokenFilter} instances operate on this to
 *         implement their behavior
 */
public class TokenStream implements Iterator<Token> {
	// Added Later
	// private Map<Token, ArrayList<String>> tokenMap;
	// Jagvir//
	Token token;
	int index;
	int dummyIndex;
	List<Token> tokenStreamList ;
	//	= new ArrayList<Token>();

	// Jagvir
	//	public TokenStream(List<Token> tokenStreamList) {
	//
	//		this.tokenStreamList = tokenStreamList;
	//	}

	public TokenStream() {
		tokenStreamList=new ArrayList<Token>();
		index=0;
		dummyIndex=0;
	}

	public List<Token> getTokenStreamList() {
		return tokenStreamList;
	}

	public void setTokenStreamList(Token token) {
		// this.tokenStreamList = tokenStreamList;
		tokenStreamList.add(token);
	}

	// Jagvir//
	/**
	 * Method that checks if there is any Token left in the stream with regards
	 * to the current pointer. DOES NOT ADVANCE THE POINTER
	 * 
	 * @return true if at least one Token exists, false otherwise
	 */

	@Override
	public boolean hasNext() {

		if(index < (tokenStreamList.size()))
			return true;
		else
			return false;
	}

	/**
	 * Method to return the next Token in the stream. If a previous hasNext()
	 * call returned true, this method must return a non-null Token. If for any
	 * reason, it is called at the end of the stream, when all tokens have
	 * already been iterated, return null
	 */
	@Override
	public Token next() {

		//		for (int index = 0; index < tokenStreamList.size(); index++) {
		//			//System.out.println(tokenStreamList.get(index));
		//			return tokenStreamList.get(index++);
		//		}
		if(hasNext())
		{
			//			currentIndex=(index)+1;
			token=(Token)tokenStreamList.get(index++);
			return token;
		}
		else
		{
			token=null;
			return token;
		}
		//		System.out.println("Test");
		//		return null;
	}

	/**
	 * Method to remove the current Token from the stream. Note that "current"
	 * token refers to the Token just returned by the next method. Must thus be
	 * NO-OP when at the beginning of the stream or at the end
	 */
	@Override
	public void remove() {
		//		index = tokenStreamList.size();
		if(index>0&&index<=tokenStreamList.size())
		{
			tokenStreamList.remove((index-1));
			index--;
			token=null;
		}
	}
	/**
	 * Method to reset the stream to bring the iterator back to the beginning of
	 * the stream. Unless the stream has no tokens, hasNext() after calling
	 * reset() must always return true.
	 */
	public void reset() {
		// TODO : YOU MUST IMPLEMENT THIS
		//		if(hasNext())
		//		{
		//				
		//		}
		index=0;
	}
	/**
	 * Method to append the given TokenStream to the end of the current stream
	 * The append must always occur at the end irrespective of where the
	 * iterator currently stands. After appending, the iterator position must be
	 * unchanged Of course this means if the iterator was at the end of the
	 * stream and a new stream was appended, the iterator hasn't moved but that
	 * is no longer the end of the stream.
	 * 
	 * @param stream
	 *            : The stream to be appended
	 */
	public void append(TokenStream stream) {
		if(stream!=null)
			tokenStreamList.addAll(stream.getTokenStreamList());
	}

	/**
	 * Method to get the current Token from the stream without iteration. The
	 * only difference between this method and {@link TokenStream#next()} is
	 * that the latter moves the stream forward, this one does not. Calling this
	 * method multiple times would not alter the return value of
	 * {@link TokenStream#hasNext()}
	 * 
	 * @return The current {@link Token} if one exists, null if end of stream
	 *         has been reached or the current Token was removed
	 */
	public Token getCurrent() {
		if(index>0 && index<=tokenStreamList.size()&&token!=null)
			return  (Token)tokenStreamList.get(index-1);
		else
			return null;
	}

	private String previous() {
		if(dummyIndex<tokenStreamList.size() && dummyIndex>=0)
			return tokenStreamList.get(dummyIndex--).getTermText();
		else 
			return null;
	}

	private String after()
	{
		if(dummyIndex<tokenStreamList.size() && dummyIndex>=0)
		{
			return tokenStreamList.get(dummyIndex++).getTermText();
		}
		else
			return null;
	}

	public List<String> getWords()
	{
		dummyIndex=index-2;
		List<String> lst=new ArrayList<String>();
		while(true)
		{
			String word=previous();
			if(word==null || word.contains("."))
				break;			
			else
			{
				lst.add(word);
			}
		}
		lst.add(getCurrent().getTermText());

		if(getCurrent().getTermText().contains("."))
			return lst;

		dummyIndex=index;

		while(true)
		{
			String word=after();
			if(word==null)
				break;
			else if(word.contains("."))
			{
				lst.add(word);
				break;
			}
			else
				lst.add(word);
		}

		return lst;
	}

	public Token getPrevious()
	{
		if(index>1 && index<=tokenStreamList.size())
			return  (Token)tokenStreamList.get(index-2);
		else
			return null;
	}
	public Token getNext(int i)
	{
		if(hasNext())
		{
			//			currentIndex=(index)+1;
			token=(Token)tokenStreamList.get(index+i);
			if(token!=null)
				return token;
			else 
				return null;
		}
		return null;
	}

	public boolean isFirst()
	{
		if(index==1)
		{
			return true;
		}
		else
			return false;
	}

	// Added later
	private Map<Token, ArrayList<String>> getTokenMap() {
		return getTokenMap();
	}

	/*
	 * public void setTokenMap(Map<Token, ArrayList<String>> tokenMap) {
	 * this.tokenMap = tokenMap; }
	 */

}
