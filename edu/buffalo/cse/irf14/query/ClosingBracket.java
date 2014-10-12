package edu.buffalo.cse.irf14.query;

import java.util.Map;

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

}
