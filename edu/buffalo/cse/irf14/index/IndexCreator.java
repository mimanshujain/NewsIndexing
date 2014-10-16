package edu.buffalo.cse.irf14.index;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;

public class IndexCreator implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Object[] String = null;
	Map<Integer, Postings> termPostings;
	Map<String, Integer> termDictionary;
	String type;
	int termId=0;
	public int docCount=0;
	transient Set<String> docIdSet;
	DocumentVector docVector;
	
	
	public IndexCreator(String type){
		termPostings=new HashMap<Integer, Postings>();
		termDictionary=new HashMap<String, Integer>();
		docIdSet=new HashSet<String>();
		this.type=type;
		docVector = null;
	}

	public DocumentVector getDocVector() {
		return docVector;
	}

	public void setDocVector(DocumentVector docVector) {
		this.docVector = docVector;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public Map<Integer, Double> getTemVector(String term)
	{
		Map<Integer, Double> termVector = new HashMap<Integer, Double>();
		if(termDictionary!=null)
		{
			if(termDictionary.containsKey(term))
			{
				int key=termDictionary.get(term);
				
				if(termPostings!=null)
				{
					if(termPostings.containsKey(key))
					{
						Postings p=termPostings.get(key);
						if(p!=null)
						{
							termVector.put(key, p.getIdf());
							return termVector;
						}
					}
				}
			}
		}
		return null;
	}
	
	public Map<String, Integer> getTermDictionary(String term) {

		if(termDictionary!=null)
		{
			if(termDictionary.containsKey(term))
			{
				int key=termDictionary.get(term);
				if(termPostings!=null)
				{
					if(termPostings.containsKey(key))
					{
						Postings p=termPostings.get(key);
						if(p!=null)
						{
							return p.getPostingMap();
						}
					}
				}
			}
		}
		return null;
	}

	public Map<Integer, Postings> getTermPostings() {
		return termPostings;
	}

	public void createIndexer(TokenStream tStream, String docId) throws IndexerException
	{
		try
		{
			docIdSet.add(docId);

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

	private void makePosting(String term, String docId) throws IndexerException
	{
		if(term!= null && !term.isEmpty())
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
					p=new Postings(term);
					p.setDocID(docId);
					termPostings.put(key, p);
				}
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

	public int getTotalDocumentValue() 
	{
		try
		{
			if(docIdSet!=null)
			{
				return docIdSet.size();
			}
			return -1;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return -1;
	}

	public int getTotalTerms()
	{
		if(termDictionary!=null)
		{
			return termDictionary.size();
		}
		return -1;
	}

	public class SortByTermFreq implements Comparator<Integer> {

		@Override
		public int compare(Integer termId1, Integer termId2) {
			int f1 = termPostings.get(termId1).collectionFreq;
			int f2 = termPostings.get(termId2).collectionFreq;
			int result=f2 - f1;

			if(result<0)
				return -1;
			else if(result > 0)
				return 1;
			else
				return termPostings.get(termId1).termString.compareTo(termPostings.get(termId2).termString);
		}

	}

	public void setDocCount() {	
		docCount=docIdSet.size();
	}

	public int getDocCount() {

		return docCount;
	}
	
	public void calculateIDF(int totalDocs)
	{
		if(termDictionary != null && termPostings != null)
		{
			String[] str = termDictionary.keySet().toArray(new String[termDictionary.size()]);
			for(String term : str)
			{
				if(termPostings.containsKey(term))
				{
					Postings p = termPostings.get(term);
					p.calculateIdf(totalDocs);
				}
			}
		}
	}
}
