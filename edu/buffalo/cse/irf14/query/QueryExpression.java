package edu.buffalo.cse.irf14.query;

import java.util.Map;
import java.util.Set;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;

public interface QueryExpression {
	public String queryInterpretor();
	public void assignOperands(QueryExpression rightEx, QueryExpression leftEx);
	public Set<String> fetchPostings(Map<IndexType,IndexReader> fetcherMap);
	public Map<String, Double> getQueryVector(Map<IndexType,IndexReader> fetcherMap);
	public String getQueryWords();
}
