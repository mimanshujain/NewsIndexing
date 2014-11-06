package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;

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

		if(sLeft == null || sLeft.isEmpty() ) return null;

		Set<String> sRight = rightOperand.fetchPostings(fetcherMap);
		if(sRight != null)
		{
			sLeft.retainAll(sRight);
			return sLeft;
		}

		return null;
	}

	@Override
	public String getQueryWords() {
		return leftOperand.getQueryWords() + " $ " + rightOperand.getQueryWords();
	}

	@Override
	public Map<String, Double> getQueryVector(Map<IndexType,IndexReader> fetcherMap) 
	{
		Map<String, Double> leftWordVector = leftOperand.getQueryVector(fetcherMap);
		Map<String, Double> rightWordVector = rightOperand.getQueryVector(fetcherMap);
		
		if(leftWordVector.isEmpty())
			leftWordVector = new HashMap<String, Double>();
		if(rightWordVector.isEmpty())
			rightWordVector = new HashMap<String, Double>();

		if(leftWordVector != null && rightWordVector != null)
		{
			
			for (Iterator<String> s = rightWordVector.keySet().iterator(); s.hasNext(); ) {
				String ss = s.next();
				if(!leftWordVector.containsKey(ss))
				{
					leftWordVector.put(ss, rightWordVector.get(ss));
				}	
				else if(leftWordVector.containsKey(ss))
				{
					Double f = leftWordVector.get(ss) + rightWordVector.get(ss);
					leftWordVector.put(ss, f);
				}		
			}	
		}
		
		return leftWordVector;
	}

	@Override
	public Map <String, List<String>> executeWildCard(Map<IndexType, IndexReader> fetcherMap) {
		Map <String, List<String>> resultL = leftOperand.executeWildCard(fetcherMap);
		Map <String, List<String>> resultR = rightOperand.executeWildCard(fetcherMap);
		if(resultL!=null && resultR!=null)
		{
			resultL.putAll(resultR);
			return resultL;
		}
		else if(resultL!=null && resultR==null)
		{			
			return resultL;
		}
		else if(resultL==null && resultR!=null)
		{
			return resultR;
		}
		return null;
	}

	@Override
	public Set<String> fetchWildPostings(Map<IndexType, IndexReader> fetcherMap) {
		Set<String> sLeft = leftOperand.fetchWildPostings(fetcherMap);

		if(sLeft == null || sLeft.isEmpty() ) return null;

		Set<String> sRight = rightOperand.fetchWildPostings(fetcherMap);
		if(sRight != null)
		{
			sLeft.retainAll(sRight);
			return sLeft;
		}

		return null;
	}

}
