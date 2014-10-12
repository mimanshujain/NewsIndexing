/**
 * 
 */
package edu.buffalo.cse.irf14.query;
/**
 * @author nikhillo
 * Static parser that converts raw text to Query objects
 */
public class QueryParser {
	/**
	 * MEthod to parse the given user query into a Query object
	 * @param userQuery : The query to parse
	 * @param defaultOperator : The default operator to use, one amongst (AND|OR)
	 * @return Query object if successfully parsed, null otherwise
	 */
	public static Query parse(String userQuery, String defaultOperator) {
		
		try
		{
			return new Query(new QueryEvaluators(userQuery.trim(), defaultOperator).queryInterpretor());
		}
		catch(Exception e) //QueryParserException -- Need to define.?
		{
			
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		String inputQuery = "A NOT B NOT (C NOT D AND E)";
		QueryEvaluators eval = new QueryEvaluators(inputQuery.trim(), "OR");
		System.out.println(eval.queryInterpretor());
	}
}
