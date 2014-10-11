package edu.buffalo.cse.irf14.query;

import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryEvaluator implements QueryExpression {

	private QueryExpression queryTerm;
	boolean isQuote;
	String inputQuery;
	Stack<QueryExpression> queryStack;
	Stack<Character> brackets;
	Pattern checkPat=null;
	Matcher matPat=null;
	
	
	public QueryEvaluator(String inputQuery) {
		this.inputQuery = inputQuery;
		queryStack = new Stack<QueryExpression>();
		brackets = new Stack<Character>();
		brackets.push('$');
		
		isQuote = false;
		checkPat=Pattern.compile("([A-za-z]+:)(.*)");
		matPat=checkPat.matcher("");
	}

	public void startEvaluation() {
		try {
			for (String token : inputQuery.split(" ")) {
				char ch = token.charAt(0);
				char ch2 = token.charAt(token.length()-1);
				if (ch != '\"' || ch != '(' ) {
					matPat.reset(token);
					if(matPat.matches())
					{
						ch=matPat.group(2).charAt(0);
						checkBracketQuote(token, ch);						
					}
					
					if(token=="OR")
					{
						
					}
					else if(token=="AND")
					{
						
					}
					else if(token=="NOT")
					{
						
					}
					else
					{
						QueryExpression word=new Word();
					}
				} 
				else
				{
					checkBracketQuote(token, ch);
				}
			}
		} catch (Exception ex) {

		}
	}

	private void checkBracketQuote(String token, char ch) {
		if (ch == '(') {

			brackets.push(ch);

		} else if (ch == ')') {
			

		} else if (ch == '\"' && !isQuote) {

		} else if (isQuote && token.charAt(token.length() - 1) == '\"') {

			isQuote = false;
		}
	}

	@Override
	public String queryInterpretor(Map<String, QueryExpression> queryCalculator) {

		return null;
	}

	// private boolean

}
