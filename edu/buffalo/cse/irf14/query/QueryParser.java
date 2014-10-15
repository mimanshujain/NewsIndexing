/**
 * 
 */
package edu.buffalo.cse.irf14.query;

import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.irf14.analysis.TermAnalyer;
import edu.buffalo.cse.irf14.index.*;

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
			if(userQuery != null && !"".equals(userQuery))
			{
				return new Query(new QueryEvaluators(userQuery.trim(), defaultOperator));
			}
			else
				throw new QueryParserException();
		}
		catch(QueryParserException e) //QueryParserException -- Need to define.?
		{
			e.printStackTrace(); 
		}
		return null;
	}

	public static void main(String[] args)
	{
		String inputQuery = "(black OR blue) AND bruises";
		QueryEvaluators eval = new QueryEvaluators(inputQuery.trim(), "OR");
		//System.out.println(eval.queryInterpretor());
		String queryWords = eval.getQueryWords();
		String indexDir = System.getProperty("Index.dir");
		IndexReader termReader = new IndexReader(indexDir, IndexType.TERM);
		IndexReader placeReader = new IndexReader(indexDir, IndexType.PLACE);
		IndexReader  authReader= new IndexReader(indexDir, IndexType.AUTHOR);
		IndexReader catReader = new IndexReader(indexDir, IndexType.CATEGORY);
		Map<IndexType,IndexReader> fetcher = new HashMap<IndexType, IndexReader>();
		
		fetcher.put(IndexType.TERM, termReader);
		fetcher.put(IndexType.PLACE, placeReader);
		fetcher.put(IndexType.CATEGORY, catReader);
		fetcher.put(IndexType.AUTHOR, authReader);
		
		eval.fetchPostings(fetcher);
	}
}
