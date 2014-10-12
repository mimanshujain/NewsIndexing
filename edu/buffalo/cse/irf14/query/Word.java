package edu.buffalo.cse.irf14.query;

import java.util.Map;

public class Word implements QueryExpression {

	private String wordVal;
//	private String indexType;
//	
	public Word(String wordVal) {
		
		if(!wordVal.contains(":"))
		{
			this.wordVal = "Term:" + wordVal;
		}
		else
			this.wordVal = wordVal;
	}

	@Override
	public void assignOperands(QueryExpression rightEx, QueryExpression leftEx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String queryInterpretor() {
		return wordVal;
	}

}
