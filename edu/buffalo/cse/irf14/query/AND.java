package edu.buffalo.cse.irf14.query;

import java.util.Map;

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
	public String queryInterpretor(Map<String, QueryExpression> queryCalculator) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void assignOperands(QueryExpression rightEx, QueryExpression leftEx)
	{
		rightOperand = rightEx;
		leftOperand = leftEx;
	}

}
