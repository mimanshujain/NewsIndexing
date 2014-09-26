package edu.buffalo.cse.irf14.index;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;

public class IndexCreator implements java.io.Serializable
{

	Map<Integer, Postings> termPostings;
	Map<String, Integer> termDictionary;
	String type;
	int termId;

	public IndexCreator(String type) {
		termId=1;
		termPostings=new HashMap<Integer, Postings>();
		termDictionary=new HashMap<String, Integer>();
		this.type=type;
	}

	public String getType() {
		return type;
	}

	public Map<String, Integer> getTermDictionary() {
		return termDictionary;
	}

	public Map<Integer, Postings> getTermPostings() {
		return termPostings;
	}

	private void createIndexer(TokenStream tStream, int docId) throws IndexerException
	{
		try
		{
			List<Token> tokenStreamList=tStream.getTokenStreamList();
			if(tokenStreamList!=null)
			{
				for(Token tk : tokenStreamList)
				{
					makePosting(tk.toString(), docId);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new IndexerException();
		}

	}

	private void makePosting(String term, int docId) throws IndexerException
	{
		Postings p;
		int key=testDict(term);
		if(key!=-1)
		{
			if(termPostings.containsKey(key))
			{
				p=termPostings.get(key);		
				p.setDocID(docId);
			}
			else
			{
				p=new Postings();
				p.setDocID(docId);
				termPostings.put(key, p);
			}
		}
	}

	private int testDict(String term) throws IndexerException
	{
		int localTermId=0;
		if(termDictionary!=null)
		{
			if(termDictionary.containsKey(term))
			{
				localTermId=termDictionary.get(term);
				return localTermId;
			}
			else
			{
				termDictionary.put(term,++termId);
				return termId;
			}
		}
		else
			return -1;

	}

}
