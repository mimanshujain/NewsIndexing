package edu.buffalo.cse.irf14.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.buffalo.cse.irf14.index.*;

/**
 * Class that represents a parsed query
 * @author nikhillo
 *
 */
public class Query {

	QueryExpression evaluateQuery = null;
	Map<String,Double> queryVector;
	Set<String> docIdList;
	
	public Set<String> getDocIdList() {
		return docIdList;
	}

	/**
	 * Constructor
	 */
	public Query(QueryExpression evaluateQuery)
	{
		this.evaluateQuery=evaluateQuery;
		queryVector =  new HashMap<String,Double>();
		docIdList = null;
	}

	/**
	 * Method to convert given parsed query into string
	 */
	public String toString() {
		if(evaluateQuery != null)			
			return evaluateQuery.queryInterpretor();
		return null;
	}

	public  void executeQuery(Map<IndexType,IndexReader> fetcherMap)
	{
		try
		{
			if(evaluateQuery != null)
			{
				docIdList = evaluateQuery.fetchPostings(fetcherMap);
				if(docIdList != null)
				{
					if(docIdList.size() > 0)
						queryVector = evaluateQuery.getQueryVector(fetcherMap);
					else
						queryVector = null;
				}
				else
					queryVector = null;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public  void executeWildQuery(Map<IndexType,IndexReader> fetcherMap)
	{
		try
		{
			if(evaluateQuery != null)
			{
				docIdList = evaluateQuery.fetchWildPostings(fetcherMap);
				if(docIdList != null)
				{
					if(docIdList.size() > 0)
						queryVector = evaluateQuery.getQueryVector(fetcherMap);
					else
						queryVector = null;
				}
				else
					queryVector = null;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public  Map <String, List<String>> executeWild(Map<IndexType,IndexReader> fetcherMap)
	{
		try
		{
			if(evaluateQuery != null)
			{
				Map <String, List<String>> wordWild;
				wordWild = evaluateQuery.executeWildCard(fetcherMap);
				if(wordWild != null)
				{
					return wordWild;
				}
				else
					return null;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	
	public Map<String, Double> getQueryVector() {
		return queryVector;
	}


	public String[] getQueryTerms()
	{
		try 
		{
			if(evaluateQuery != null)
			{
				String query = evaluateQuery.getQueryWords().toString();
//				query.replaceAll(" $ ", " ");
				String[] queryWords = query.split(" \\$ ");
				
				return queryWords;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
}
