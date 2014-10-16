package edu.buffalo.cse.irf14.query;

import java.util.Map;
import java.util.Set;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.Postings;

public class AND implements QueryExpression {

	QueryExpression leftOperand;
	QueryExpression rightOperand;

	public AND() {
		// TODO Auto-generated constructor stub
	}

	public void setLeftOperand(QueryExpression leftOperand) {
		this.leftOperand = leftOperand;
	}

	public void setRightOperand(QueryExpression rightOperand) {
		this.rightOperand = rightOperand;
	}

	@Override
	public void assignOperands(QueryExpression rightEx, QueryExpression leftEx)
	{
		rightOperand = rightEx;
		leftOperand = leftEx;
	}

	@Override
	public String queryInterpretor() {
		return leftOperand.queryInterpretor() + " AND " + rightOperand.queryInterpretor();
	}

	@Override
	public Set<String> fetchPostings(Map<IndexType, IndexReader> fetcherMap) {
		Set<String> sLeft = leftOperand.fetchPostings(fetcherMap);
		Set<String> sRight = rightOperand.fetchPostings(fetcherMap);
		sLeft.retainAll(sRight);
		return sLeft;
	}

	@Override
	public String getQueryWords() {
		return leftOperand.getQueryWords() + "$" + rightOperand.getQueryWords();
	}

	@Override
	public Map<Integer, Double> getQueryVector() {
		Map<Integer, Double> leftWordVector = leftOperand.getQueryVector();
		Map<Integer, Double> rightWordVector = leftOperand.getQueryVector();
		leftWordVector.keySet().containsAll(rightWordVector.keySet());
		return leftWordVector;
	}

}
