package edu.buffalo.cse.irf14.query;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.Postings;

public class Word implements QueryExpression {

	private String wordVal;
	private String indexType;
	private Map<String, Integer> postings;
	
	public Word(String wordVal) {
	
		this.wordVal =  wordVal;
		
		if(!wordVal.contains(":"))
		{
			indexType = "Term";
		}
		else
		{
			int index = wordVal.indexOf(":");
			indexType = wordVal.substring(0,index);
			this.wordVal = wordVal.substring(index+1, wordVal.length());
		}
		postings = null;
	}

	@Override
	public void assignOperands(QueryExpression rightEx, QueryExpression leftEx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String queryInterpretor() {
		return indexType + ":" + wordVal;
	}
	
	public Set<String> fetchPostings(Map<IndexType,IndexReader> fetcherMap)
	{
		try
		{
			IndexReader reader = null;
			if(IndexType.TERM.name().equals(indexType.toUpperCase()))
			{
				reader = fetcherMap.get(IndexType.TERM);
			}
			else if(IndexType.CATEGORY.name().equals(indexType.toUpperCase()))
			{
				reader = fetcherMap.get(IndexType.CATEGORY);
			}
			else if(IndexType.AUTHOR.name().equals(indexType.toUpperCase()))
			{
				reader = fetcherMap.get(IndexType.AUTHOR);
			}
			else if(IndexType.PLACE.name().equals(indexType.toUpperCase()))
			{
				reader = fetcherMap.get(IndexType.PLACE);
			}
				
			if(reader != null)
			{
				postings = reader.getPostings(wordVal);
				return postings.keySet();
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	public Map<String, Integer> getPostings() {
		return postings;
	}

	@Override
	public String getQueryWords() {
		return wordVal;
	}

}
