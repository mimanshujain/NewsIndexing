package edu.buffalo.cse.irf14.query;

import java.util.Map;

public interface QueryExpression {
	public String queryInterpretor();
	public void assignOperands(QueryExpression rightEx, QueryExpression leftEx);
}
