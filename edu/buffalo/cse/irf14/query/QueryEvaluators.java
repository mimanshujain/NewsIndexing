package edu.buffalo.cse.irf14.query;

import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.ldap.StartTlsRequest;

import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;

public class QueryEvaluators implements QueryExpression {

	QueryExpression evluator;
	
	public QueryEvaluators(String inputQuery) {

		try
		{
			if (inputQuery != null && !"".equals(inputQuery)) {

				Tokenizer tokenStart = new Tokenizer();
				TokenStream stream =  tokenStart.consume(inputQuery);

				if(stream != null)
				{
					Stack<QueryExpression> wordStack = new Stack<QueryExpression>();
					Stack<QueryExpression> operatorStack = new Stack<QueryExpression>();

					QueryExpression opening = new OpeningBracket();
					operatorStack.push(opening);
					inputQuery = inputQuery + ")";

					int countToken = 0;
					boolean isWord=false, isQuote = false;

					String tempToken = "";

					OperatorFactory opFactIntance = OperatorFactory.getInstance();

					Pattern checkPat=Pattern.compile("([A-za-z]+:)(\\()(.*)");
					Matcher matPat=checkPat.matcher("");

					while (stream.hasNext()) 
					{						
						String token = stream.next().toString();
						matPat.reset(token);

						if(matPat.matches())
						{
							tempToken = matPat.group(2) + matPat.group(1) + matPat.group(3);
							int index = stream.getIndex();
							int i = 0;
							
							while(true)
							{
								Token tk = stream.getNext(index + i);
								if(tk != null && !"".equals(tk.toString()))
								{
									if(tk.toString().contains(")"))
									{
										tk.setTermText(matPat.group(1) + tk.toString());
										break;
									}
									else if(tk.toString().toUpperCase() != OperatorType.AND.name() && tk.toString().toUpperCase() != OperatorType.OR.name()
											&& tk.toString().toUpperCase() != OperatorType.NOT.name())
									{
										tk.setTermText(matPat.group(1) + tk.toString());
									}
								}
								i++;
							}
						}

						char chStart = token.charAt(0);
						char chEnd = token.charAt(token.length() - 1);

						if (chStart != '\"' && chStart != '(' && chEnd != '\"'
								&& chEnd == ')' && !isQuote) 
						{
							if ((token.toUpperCase() == OperatorType.OR.name()
									|| token.toUpperCase() == OperatorType.AND.name()
									|| token.toUpperCase() == OperatorType.NOT.name()) && isWord) 
							{

								operatorStack.push(opFactIntance
										.getOperatorByType(token.toUpperCase()));
								isWord=false;
							} 
							else 
							{
								if(isWord == true) //default OR
								{														
									operatorStack.push(opFactIntance.getOperatorByType("OR"));							
								}

								wordStack.push(new Word(token));
								isWord=true;
							}
						}

						//Starting with opening Bracket
						else if(chStart == '(')
						{
							operatorStack.push(new OpeningBracket());
							token = token.substring(1);
							wordStack.push(new Word(token));
							isWord=true;
						}

						else if(chEnd == ')')
						{
							tempToken = token.substring(0,token.length()-2);
							wordStack.push(tempToken);
							QueryExpression closing = new ClosingBracket();
							QueryExpression qExp;

							while(true)
							{
								if(!operatorStack.empty())
								{
									qExp=operatorStack.pop();
									if(qExp instanceof OpeningBracket)
									{
										closing.assignOperands(wordStack.pop(), qExp);
										wordStack.push(closing);
									}
									else
									{
										QueryExpression leftOperand = null;
										QueryExpression rightOperand = null;
										//QueryExpression operator = null;

										if(!wordStack.empty())
										{
											rightOperand = wordStack.pop();
											leftOperand = wordStack.pop();
											qExp.assignOperands(rightOperand, leftOperand);
											wordStack.push(qExp);
										}
									}
								}
							}

						}

						else if(chStart == '\"' && chEnd != '\"')
						{							
							tempToken = token + " ";
							isQuote=true;					
						}

						else if(chStart == '\"' && chEnd == '\"')
						{
							wordStack.push(new Word(token));
						}

						else if(chEnd == '\"' && isQuote)
						{
							tempToken = tempToken + token;
							wordStack.push(new Word(tempToken));
						}
						
						else if(isQuote)
						{
							tempToken = tempToken + token;
						}
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public String queryInterpretor(Map<String, QueryExpression> queryCalculator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void assignOperands(QueryExpression rightEx, QueryExpression leftEx) {
		// TODO Auto-generated method stub
		
	}

}
