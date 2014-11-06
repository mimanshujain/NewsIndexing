package edu.buffalo.cse.irf14.index;

import java.util.Map;

import edu.buffalo.cse.irf14.query.Query;

public class IndexSearcher {

	private Query objQuery;
	String[] queryTerms;

	public IndexSearcher(Query objQuery) {
		this.objQuery = objQuery;
	}
	
	public Query getObjQuery() {
		return objQuery;
	}

	public String[] getQueryTerms() 
	{		
		if(objQuery != null)
		{
			queryTerms = objQuery.getQueryTerms();
		}
		else
			queryTerms = null;
		
		return queryTerms;
	}
	
	public void executeQuery(Map<IndexType,IndexReader> fetcherMap)
	{
		objQuery.executeQuery(fetcherMap);
	}
	
	public void executeWildQuery(Map<IndexType,IndexReader> fetcherMap)
	{
		objQuery.executeWildQuery(fetcherMap);
	}
}
