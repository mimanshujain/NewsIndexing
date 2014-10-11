package edu.buffalo.cse.irf14.query;

import java.util.Map;

public interface QueryExpression {
	public String queryInterpretor(Map<String, QueryExpression> queryCalculator);
	public void assignOperands(QueryExpression rightEx, QueryExpression leftEx);
}
