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

	QueryExpression evaluator;

	public QueryEvaluators(String inputQuery, String defaultOperator) {

		try
		{
			if (inputQuery != null && !"".equals(inputQuery)) {

//				inputQuery = inputQuery + " )";
				Tokenizer tokenStart = new Tokenizer();
				TokenStream stream =  tokenStart.consume(inputQuery);
				Token closingToken = new Token();
				closingToken.setTermText(" )");
				stream.setTokenStreamList(closingToken);
				
				if(stream != null)
				{
					Stack<QueryExpression> wordStack = new Stack<QueryExpression>();
					Stack<QueryExpression> operatorStack = new Stack<QueryExpression>();

					QueryExpression opening = new OpeningBracket();
					operatorStack.push(opening);				

					int countToken = 0;
					boolean isWord=false, isQuote = false;

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

							if(chStart == '\"' && chEnd != '\"' && chEnd != ')')
							{							
								int index = stream.getIndex();
								int i = 0;
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
								//							isQuote=true;					
							}

							else if(chStart == '\"' && chEnd == '\"')
							{
								wordStack.push(new Word(token));
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
									if(isWord == true) //default OR
									{														
										operatorStack.push(opFactIntance.getOperatorByType(defaultOperator.toUpperCase()));							
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
							//						else if(chStart == '\"' && chEnd != '\"')
							//						{							
							//							int index = stream.getIndex();
							//							int i = 0;
							//							tempToken = token + " ";
							//							while(true)
							//							{
							//								Token tk = stream.getNext(index);
							//								if(tk != null && !"".equals(tk.toString()))
							//								{
							//									if(tk.toString().contains("\""))
							//									{
							//										tempToken = tempToken + tk.toString();
							//										stream.remove(index + 1);
							//										break;
							//									}
							//									tempToken = tempToken + " ";
							//									stream.remove(index + 1);
							//								}
							//							}
							//							
							//							isQuote=true;					
							//						}

							//						else if(chStart == '\"' && chEnd == '\"')
							//						{
							//							wordStack.push(new Word(token));
							//						}

							//						else if(chEnd == '\"' && isQuote)
							//						{
							//							tempToken = tempToken + token;
							//							wordStack.push(new Word(tempToken));
							//						}
							//
							//						else if(isQuote)
							//						{
							//							tempToken = tempToken + token;
							//						}
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

}

