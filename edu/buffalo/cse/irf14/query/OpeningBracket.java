package edu.buffalo.cse.irf14.query;

import java.util.Map;

public class OpeningBracket implements QueryExpression {

	QueryExpression leftOperand;
	QueryExpression rightOperand;
	
	public OpeningBracket() {
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
		return "[ ";
	}
}
