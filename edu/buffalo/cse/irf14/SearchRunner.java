package edu.buffalo.cse.irf14;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.buffalo.cse.irf14.index.DocumentVector;
import edu.buffalo.cse.irf14.index.IndexCreator;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexSearcher;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.Postings;
import edu.buffalo.cse.irf14.index.Scorer;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;

/**
 * Main class to run the searcher.
 * As before implement all TODO methods unless marked for bonus
 * @author nikhillo
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

		objQuery = QueryParser.parse(userQuery, "OR");

		if(objQuery != null && fetcher != null)
		{
			searcher = new IndexSearcher(objQuery);
			searcher.executeQuery(fetcher);
		}
		Scorer score = new Scorer(ScoringModel.TFIDF);
		TreeMap<String, Double> relevancyScore = score.getOrderedDocuments(objQuery, docVector);
		
		Iterator<String> iterDocId = relevancyScore.keySet().iterator();
		
		while(iterDocId.hasNext())
		{
			String docId = iterDocId.next();
			
			System.out.println("Document Id:: " + docId + "  Score:: "  + relevancyScore.get(docId));
		}
	}

	/**
	 * Method to execute queries in E mode
	 * @param queryFile : The file from which queries are to be read and executed
	 */
	public void query(File queryFile) {
		
	}

	/**
	 * General cleanup method
	 */
	public void close() {
		//TODO : IMPLEMENT THIS METHOD
	}

	/**
	 * Method to indicate if wildcard queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean wildcardSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF WILDCARD BONUS ATTEMPTED
		return false;
	}

	/**
	 * Method to get substituted query terms for a given term with wildcards
	 * @return A Map containing the original query term as key and list of
	 * possible expansions as values if exist, null otherwise
	 */
	public Map<String, List<String>> getQueryTerms() {
		//TODO:IMPLEMENT THIS METHOD IFF WILDCARD BONUS ATTEMPTED
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
