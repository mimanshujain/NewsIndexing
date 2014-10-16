package edu.buffalo.cse.irf14.query;

import java.util.HashMap;
import java.util.HashSet;
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

	public  void setDocIdSet(Map<IndexType,IndexReader> fetcherMap)
	{
		try
		{
			if(evaluateQuery != null)
			{
				docIdList = evaluateQuery.fetchPostings(fetcherMap);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public Map<String, Double> getQueryVector() {
		return queryVector;
	}

	private void setQueryVector(Map<String, Double> queryVector) {
		
	}

	public String[] getQueryTerms()
	{
		try 
		{
			if(evaluateQuery != null)
			{
				return evaluateQuery.getQueryWords().split("$");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
}
