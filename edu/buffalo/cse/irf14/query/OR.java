package edu.buffalo.cse.irf14.query;

import java.util.Map;

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
}
