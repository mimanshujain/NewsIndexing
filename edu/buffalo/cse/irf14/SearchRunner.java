package edu.buffalo.cse.irf14;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.ParseConversionEvent;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.DocumentVector;
import edu.buffalo.cse.irf14.index.IndexCreator;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexSearcher;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.Postings;
import edu.buffalo.cse.irf14.index.Scorer;
import edu.buffalo.cse.irf14.query.EvaluateParser;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;
import edu.buffalo.cse.irf14.query.QueryParserException;

/**
 * Main class to run the searcher.
 * As before implement all TODO methods unless marked for bonus
 *
 */
public class SearchRunner {
	public enum ScoringModel {TFIDF, OKAPI};

	private Query objQuery;
	private char mode;
	PrintStream stream;
	IndexSearcher searcher;
	Map<IndexType,IndexReader> fetcher;
	IndexReader termReader;
	IndexReader placeReader;
	IndexReader  authReader;
	IndexReader catReader;
	DocumentVector docVector;
	/**
	 * Default (and only public) constructor
	 * @param indexDir : The directory where the index resides
	 * @param corpusDir : Directory where the (flattened) corpus resides
	 * @param mode : Mode, one of Q or E
	 * @param stream: Stream to write output to
	 */
	public SearchRunner(String indexDir, String corpusDir, 
			char mode, PrintStream stream) {

		System.setProperty("Index.dir", indexDir);
		System.setProperty("corpus.dir", corpusDir);

		searcher = null;

		this.mode = mode;
		this .stream = stream;

		fetcher = new HashMap<IndexType, IndexReader>();
		termReader = new IndexReader(indexDir, IndexType.TERM);
		placeReader = new IndexReader(indexDir, IndexType.PLACE);
		authReader= new IndexReader(indexDir, IndexType.AUTHOR);
		catReader = new IndexReader(indexDir, IndexType.CATEGORY);

		fetcher.put(IndexType.TERM, termReader);
		fetcher.put(IndexType.PLACE, placeReader);
		fetcher.put(IndexType.CATEGORY, catReader);
		fetcher.put(IndexType.AUTHOR, authReader);
		docVector = termReader.getDocVector();
	}

	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model) {
		try
		{
			long lStartTime = System.currentTimeMillis();
			objQuery = QueryParser.parse(userQuery, "OR");
			if(objQuery != null && fetcher != null)
			{
				searcher = new IndexSearcher(objQuery);
				if(!userQuery.contains("*"))
					searcher.executeQuery(fetcher);
				else
				{
					searcher.executeWildQuery(fetcher);
					int i;
				}
			}
			Scorer score = new Scorer(model);
			//		score.getOrderedDocuments(objQuery, docVector);

			TreeMap<String, Double> relevancyScore = score.getOrderedDocuments(objQuery, docVector);
			stream.println(userQuery);
			long lEndTime = System.currentTimeMillis();
			long difference = lEndTime - lStartTime;
			stream.println("The Time taken to execute the query(in ms) :: " + difference);

			printResult(relevancyScore);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void printResult(TreeMap<String, Double> relevancyScore) 
	{
		if(relevancyScore!= null && !relevancyScore.isEmpty())
		{
			Iterator<String> iterDocId = relevancyScore.keySet().iterator();
			StringBuilder sb = new StringBuilder();
			int counter = 1;
			Document d = null;
			String[] qTerms = objQuery.getQueryTerms();

			while(iterDocId.hasNext())
			{
				if(counter > 10)
					break;
				String docId = iterDocId.next();
				sb.append(System.getProperty("line.separator"));
				sb.append("Rank: " + counter+"\n");
				sb.append(System.getProperty("line.separator"));
				try {
					d = Parser.parse(System.getProperty("corpus.dir")+File.separator+docId);
					if(d!=null)
					{
						if(d.getField(FieldNames.TITLE) != null)
						{
							sb.append("Title: "+d.getField(FieldNames.TITLE)[0] + "\n");
							sb.append(System.getProperty("line.separator"));
						}
						else
						{
							sb.append("No Title for this doc\n");
						}

						StringBuilder snip = new StringBuilder("..");
						if(d.getField(FieldNames.CONTENT) != null)
						{
							String content = d.getField(FieldNames.CONTENT)[0];
							int lastValid = 0;
							for(String str : qTerms)
							{
								int lastIndex = 0;
								if(content.contains(str))
								{
									while(lastIndex !=-1)
									{
										int index = content.indexOf(str, lastIndex+1);
										if(index != -1)
										{
											if(index >= 10)
											{
												snip.append(content.substring(index - 7, index + str.length()+10)+ "..");
											}
											else
											{
												snip.append(content.substring(index, index + str.length()+10)+ "..");
											}
											lastValid = index;
										}
										lastIndex = index;
									}
								}
							}

							if(snip.length() == 0)
							{
								if(content.length()>30)
									snip.append(content.substring(lastValid,30));
								else
									snip.append(content);
							}
							else if(snip.length() < 10 && snip.length()!=0)
							{
								if(content.length()>20)
									snip.append(content.substring(lastValid,20));
								else
									snip.append(".."+content);
							}
//							else if(snip.length() > 30)
//							{
//								
//							}
						}
						sb.append(snip);sb.append("\n");
						sb.append(System.getProperty("line.separator"));
						sb.append("\n\nRelevency Score: "+relevancyScore.get(docId));
						sb.append(System.getProperty("line.separator"));
						sb.append(System.getProperty("line.separator"));
					}
				} 
				catch (ParserException e) {	
					e.printStackTrace();
				}
				counter++;

			}
			stream.println(sb.toString());
			stream.println("\n");
		}

	}

	/**
	 * Method to execute queries in E mode
	 * @param queryFile : The file from which queries are to be read and executed
	 */
	public void query(File queryFile) {
		try {
			EvaluateParser.parseQueries(queryFile);
//			int numQ = EvaluateParser.numQ;
			int counter = 0;
			Map<String,TreeMap<String, Double>> res = new HashMap<String, TreeMap<String,Double>>();
			for(String query : EvaluateParser.query)
			{
				Query obj= QueryParser.parse(query, "OR");

				if(obj != null && fetcher != null)
				{
					searcher = new IndexSearcher(obj);
					searcher.executeQuery(fetcher);
				}

				Scorer TFIDFscore = new Scorer(ScoringModel.TFIDF);

				TreeMap<String, Double> TFIDFrelevancyScore = TFIDFscore.getOrderedDocuments(obj, docVector);
				if(!TFIDFrelevancyScore.isEmpty())
				{
					res.put(EvaluateParser.qid[counter], TFIDFrelevancyScore);
				}
				counter++;
			}
			counter = 0;
			Iterator<String> it = res.keySet().iterator();
			stream.append("numResults="+res.size());
			stream.println("\n");
			while(it.hasNext())
			{
				String id =it.next();
				TreeMap<String, Double> TFIDFrelevancyScore = res.get(id);
				Iterator<String> it2 = TFIDFrelevancyScore.keySet().iterator();
				StringBuilder sb = new StringBuilder();
				int count = 0;
				while(it2.hasNext())
				{
					if(count++<10)
					{
						String doc = it2.next();
						sb.append(doc+"#"+TFIDFrelevancyScore.get(doc)+", ");
					}
					else
						break;
				}
				sb.append(" ");
				String fin = sb.toString().replaceAll(",  " ,"}");
				stream.append(id+":{"+fin);
				stream.println("\n");
			}		

		} catch (QueryParserException e) {

			e.printStackTrace();
		}

	}

	/**
	 * General cleanup method
	 */
	public void close() {

	}

	//	private int[] findAllIndex(String content, String str)
	//	{
	//		int[] index;
	//	}

	/**
	 * Method to indicate if wildcard queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean wildcardSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF WILDCARD BONUS ATTEMPTED
		return true;
	}

	/**
	 * Method to get substituted query terms for a given term with wildcards
	 * @return A Map containing the original query term as key and list of
	 * possible expansions as values if exist, null otherwise
	 */
	public Map<String, List<String>> getQueryTerms() {
		if(objQuery!=null)
		{
			return objQuery.executeWild(fetcher);
		}
		return null;

	}

	/**
	 * Method to indicate if speel correct queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean spellCorrectSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF SPELLCHECK BONUS ATTEMPTED
		return false;
	}

	/**
	 * Method to get ordered "full query" substitutions for a given misspelt query
	 * @return : Ordered list of full corrections (null if none present) for the given query
	 */
	public List<String> getCorrections() {
		//TODO: IMPLEMENT THIS METHOD IFF SPELLCHECK EXECUTED
		return null;
	}
}
