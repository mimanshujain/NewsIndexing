package edu.buffalo.cse.irf14.query;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;

public class OR implements QueryExpression {

	QueryExpression leftOperand;
	QueryExpression rightOperand;

	public OR() {
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

		return leftOperand.queryInterpretor() + " OR " + rightOperand.queryInterpretor();

	}

	@Override
	public Set<String> fetchPostings(Map<IndexType, IndexReader> fetcherMap) {
		
		Set<String> sLeft = leftOperand.fetchPostings(fetcherMap);
		Set<String> sRight = rightOperand.fetchPostings(fetcherMap);

		if(sLeft != null && sRight != null)
		{
			sLeft.addAll(sRight);
			return sLeft;
		}
		else if(sLeft != null && sRight == null)
			return sLeft;
		else	if(sLeft == null && sRight != null)	
			return sRight;

		return null;
	}

	@Override
	public String getQueryWords() {
		return leftOperand.getQueryWords() + "$" + rightOperand.getQueryWords();
	}

	@Override
	public Map<String, Double> getQueryVector(Map<IndexType,IndexReader> fetcherMap) {
		Map<String, Double> leftWordVector = leftOperand.getQueryVector(fetcherMap);
		Map<String, Double> rightWordVector = rightOperand.getQueryVector(fetcherMap);
		
		if(leftWordVector.isEmpty())
			leftWordVector = new HashMap<String, Double>();
		if(rightWordVector.isEmpty())
			rightWordVector = new HashMap<String, Double>();

		if(leftWordVector != null && rightWordVector != null)
		{
			leftWordVector.keySet().addAll(rightWordVector.keySet());
			
			for (Iterator<String> s = leftWordVector.keySet().iterator(); s.hasNext(); ) {
				String ss = s.next();
				Double f = leftWordVector.get(ss) + rightWordVector.get(ss);
				leftWordVector.put(ss, f);
			}
			
//			
		}
//		else if(leftWordVector != null && rightWordVector == null)
//			return leftWordVector;
//		else if(leftWordVector == null && rightWordVector != null)
//			return leftWordVector;
		
		return leftWordVector;
	}
}
