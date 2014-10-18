package edu.buffalo.cse.irf14.query;

import java.util.Map;
import java.util.Set;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;

public class OpeningBracket implements QueryExpression {

	QueryExpression leftOperand;
	QueryExpression rightOperand;
	
	public OpeningBracket() {

	}

	@Override
	public void assignOperands(QueryExpression rightEx, QueryExpression leftEx)
	{
		rightOperand = rightEx;
		leftOperand = leftEx;
	}

	@Override
	public String queryInterpretor() {
		return "[ ";
	}

	@Override
	public Set<String> fetchPostings(Map<IndexType, IndexReader> fetcherMap) {
		
		return null;
	}

	@Override
	public String getQueryWords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Double> getQueryVector(Map<IndexType, IndexReader> fetcherMap) {
		// TODO Auto-generated method stub
		return null;
	}
}
