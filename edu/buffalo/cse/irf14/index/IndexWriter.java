/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import edu.buffalo.cse.irf14.analysis.SymbolFilter;
import edu.buffalo.cse.irf14.analysis.TokenFilter;
import edu.buffalo.cse.irf14.analysis.TokenFilterFactory;
import edu.buffalo.cse.irf14.analysis.TokenFilterType;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo Class responsible for writing indexes to disk
 */
public class IndexWriter {
	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *            : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		// TODO : YOU MUST IMPLEMENT THIS
	}

	/**
	 * Method to add the given Document to the index This method should take
	 * care of reading the filed values, passing them through corresponding
	 * analyzers and then indexing the results for each indexable field within
	 * the document.
	 * 
	 * @param d
	 *            : The Document to be added
	 * @throws IndexerException
	 *             : In case any error occurs
	 * @throws TokenizerException
	 */
	public void addDocument(Document d) throws IndexerException,
			TokenizerException {
		// TODO : YOU MUST IMPLEMENT THIS
		// Mimanshu Starts
		Tokenizer newToken = new Tokenizer();
		String[] termString = { d.getField(FieldNames.TITLE)[0],
				d.getField(FieldNames.CONTENT)[0] };
		TokenStream termStream = new TokenStream();
		for (String term : termString) {
			termStream = newToken.consume(term);

		}
		// For Dictionary
		// List<List<String>> super2dArray = new ArrayList<ArrayList<String>>()
		// Mimanshu end

	}

	private void doAnalysisOnStream(TokenStream tStream) throws IndexerException {
//		SymbolFilter symFilter=new SymbolFilter();
		try
		{
			TokenFilterFactory tFilterFactory=TokenFilterFactory.getInstance();
			TokenFilter symFilter=tFilterFactory.getFilterByType(TokenFilterType.SYMBOL, tStream);
			if(symFilter!=null)
			{
				
			}
			else
			{
				//Not sure what to throw.
			}
		}
		catch(IndexerException iEx)
		{
			iEx.printStackTrace();
		}
		
	}

	/**
	 * Method that indicates that all open resources must be closed and cleaned
	 * and that the entire indexing operation has been completed.
	 * 
	 * @throws IndexerException
	 *             : In case any error occurs
	 */
	public void close() throws IndexerException {
		// TODO
	}
}
