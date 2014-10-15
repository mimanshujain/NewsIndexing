package edu.buffalo.cse.irf14.query;

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
	public Query(QueryExpression evaluateQuery)
	{
		this.evaluateQuery=evaluateQuery;
	}

	/**
	 * Method to convert given parsed query into string
	 */
	public String toString() {
		if(evaluateQuery != null)			
			return evaluateQuery.queryInterpretor();
		return null;
	}

	public Set<String> getDocIdSet(Map<IndexType,IndexReader> fetcherMap)
	{
		try
		{
			if(evaluateQuery != null)
			{
				return evaluateQuery.fetchPostings(fetcherMap);
			}
			else 
				return null;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
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
