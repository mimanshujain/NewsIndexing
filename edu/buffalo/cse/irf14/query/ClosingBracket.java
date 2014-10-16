package edu.buffalo.cse.irf14.query;

import java.util.Map;
import java.util.Set;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;

public class ClosingBracket implements QueryExpression {

	QueryExpression leftOperand;
	QueryExpression rightOperand;
	
	public ClosingBracket() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void assignOperands(QueryExpression rightEx, QueryExpression leftEx)
	{
		rightOperand = rightEx;
		leftOperand = leftEx;
	}

	@Override
	public String queryInterpretor() {
		return leftOperand.queryInterpretor() + rightOperand.queryInterpretor() + " ]";
	}

	@Override
	public Set<String> fetchPostings(Map<IndexType, IndexReader> fetcherMap) {
		// TODO Auto-generated method stub
		return rightOperand.fetchPostings(fetcherMap);
	}

	@Override
	public String getQueryWords() {
			return rightOperand.getQueryWords();
	}

	@Override
	public Map<Integer, Double> getQueryVector() {
		return rightOperand.getQueryVector();
	}

}
