package edu.buffalo.cse.irf14.query;

import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;

public class QueryEvaluators implements QueryExpression {

	QueryExpression evaluator;
	private static int counter = 0;
	public QueryEvaluators(String inputQuery, String defaultOperator) {

		try
		{
			if (inputQuery != null && !"".equals(inputQuery)) {

				Tokenizer tokenStart = new Tokenizer();
				TokenStream stream =  tokenStart.consume(inputQuery);
				preProcessQuery(stream);
				stream.reset();
				Token closingToken = new Token();
				closingToken.setTermText(" )");
				stream.setTokenStreamList(closingToken);

				if(stream != null)
				{
					Stack<QueryExpression> wordStack = new Stack<QueryExpression>();
					Stack<QueryExpression> operatorStack = new Stack<QueryExpression>();

					QueryExpression opening = new OpeningBracket();
					operatorStack.push(opening);				

					boolean isWord=false;

					String tempToken = "";

					OperatorFactory opFactIntance = OperatorFactory.getInstance();

					Pattern checkPat=Pattern.compile("([A-za-z]+:)(\\()(.*)");
					Matcher matPat=checkPat.matcher("");

					while (stream.hasNext()) 
					{					
						Token currentToken = stream.next();
						if(currentToken != null)
						{
							String token = currentToken.toString();

							char chStart = token.charAt(0);
							char chEnd = token.charAt(token.length() - 1);

							if(token.contains("\"")&& chEnd != '\"' && chEnd != ')')
							{							
								int index = stream.getIndex();
								tempToken = token + " ";
								while(true)
								{
									Token tk = stream.getNext(index);
									if(tk != null && !"".equals(tk.toString()))
									{
										if(tk.toString().contains("\""))
										{
											tempToken = tempToken + tk.toString();										
											stream.remove(index + 1);
											currentToken.setTermText(tempToken);
											//stream.setTokenStreamList(currentToken);
											break;
										}
										tempToken = tempToken + " ";
										stream.remove(index + 1);
									}
								}			
							}

							token = currentToken.toString();
							matPat.reset(token);

							if(matPat.matches())
							{
								tempToken = matPat.group(2) + matPat.group(1) + matPat.group(3);
								int index = stream.getIndex();
								int i = 0;
								currentToken.setTermText(tempToken);
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
										else if(!tk.toString().toUpperCase().equals(OperatorType.AND.name()) && !tk.toString().toUpperCase().equals(OperatorType.OR.name())
												&& !tk.toString().toUpperCase().equals(OperatorType.NOT.name()))
										{
											tk.setTermText(matPat.group(1) + tk.toString());
										}
									}
									i++;
								}
							}
							token = stream.getCurrent().toString();
							chStart = token.charAt(0);
							chEnd = token.charAt(token.length() - 1);

							if (chStart != '(' && chEnd != ')') 
							{
								if ((token.toUpperCase().equals(OperatorType.OR.name()) || token.toUpperCase().equals(OperatorType.AND.name()) 
										|| token.toUpperCase().equals(OperatorType.NOT.name())) && isWord) 
								{

									operatorStack.push(opFactIntance
											.getOperatorByType(token.toUpperCase()));
									isWord=false;
								} 
								else 
								{
									if(isWord == true) 
									{														
										operatorStack.push(opFactIntance.getOperatorByType(defaultOperator.toUpperCase()));							
									}

									wordStack.push(new Word(token));
									if(operatorStack.peek() instanceof NOT)
									{
										QueryExpression not = operatorStack.pop();
										not.assignOperands(wordStack.pop(), wordStack.pop());
										wordStack.push(not);
									}
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

							else if(chEnd == ')' && chStart != ' ')
							{
								tempToken = token.substring(0,token.length()-1);

								if(isWord == true) //default OR
								{														
									operatorStack.push(opFactIntance.getOperatorByType(defaultOperator.toUpperCase()));							
								}
								wordStack.push(new Word(tempToken));
								QueryExpression closing = new ClosingBracket();
								QueryExpression qExp;

								while(true)
								{
									if(!operatorStack.empty())
									{
										qExp=operatorStack.pop();
										if(qExp != null)
										{
											if(qExp instanceof OpeningBracket)
											{
												closing.assignOperands(wordStack.pop(), qExp);
												wordStack.push(closing);
												isWord = true;
												break;
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

									else break;
								}

							}

							else if(token == " )")
							{
								QueryExpression closing = new ClosingBracket();
								QueryExpression qExp;

								while(true)
								{
									if(!operatorStack.empty())
									{
										qExp=operatorStack.pop();
										if(qExp != null)
										{
											if(qExp instanceof OpeningBracket)
											{
												closing.assignOperands(wordStack.pop(), qExp);
												wordStack.push(closing);
												break;
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

									else break;
								}
							}
						}
					}
					evaluator = wordStack.pop();
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void preProcessQuery(TokenStream tStream)
	{
		if(tStream != null)
		{
			counter = 0;
			int count = 0;

			while (tStream.hasNext()) 
			{					
				Token tk = tStream.next();
				if(tk != null)
				{
					if(!tk.toString().toUpperCase().equals(OperatorType.AND.name()) && !tk.toString().toUpperCase().equals(OperatorType.OR.name())
							&& !tk.toString().toUpperCase().equals(OperatorType.NOT.name()))
					{
						counter++;		
						count = counter;
					}
					else 
						counter = 0;
				}

				if(counter == 0 && count > 2 && tStream.hasNext())
				{
					int index = tStream.getIndex();
					Token t = tStream.getPrevious(index-count-1);
					if(t != null)
					{
						t.setTermText("("+t.toString());
						t = tStream.getPrevious(index-2);
						t.setTermText(t.toString()+")");
					}
				}
				else if(counter > 2 && !tStream.hasNext())
				{
					int index = tStream.getIndex();
					Token t = tStream.getPrevious(index-count);
					if(t != null)
					{
						t.setTermText("("+t.toString());
						t = tStream.getPrevious(index-1);
						t.setTermText(t.toString()+")");
					}
				}
			}
		}
	}

	@Override
	public String queryInterpretor() //Map<String, QueryExpression> queryCalculator
	{
		String preAnswer = evaluator.queryInterpretor();
		String postAnswer = "{" + preAnswer.substring(1,preAnswer.length()-1) + "}";
		return postAnswer;	
	}

	@Override
	public void assignOperands(QueryExpression rightEx, QueryExpression leftEx) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<String> fetchPostings(Map<IndexType, IndexReader> fetcherMap) {

		return evaluator.fetchPostings(fetcherMap);
	}

	@Override
	public String getQueryWords() {
		return evaluator.getQueryWords();
	}

	@Override
	public Map<String, Double> getQueryVector(Map<IndexType,IndexReader> fetcherMap) {
		// TODO Auto-generated method stub
		return evaluator.getQueryVector(fetcherMap);
	}

}

