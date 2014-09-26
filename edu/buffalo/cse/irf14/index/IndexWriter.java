/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.TermAnalyer;
import edu.buffalo.cse.irf14.analysis.TokenFilter;
import edu.buffalo.cse.irf14.analysis.TokenFilterFactory;
import edu.buffalo.cse.irf14.analysis.TokenFilterType;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo
 * Class responsible for writing indexes to disk
 */
public class IndexWriter {
	/**
	 * Default constructor
	 * @param indexDir : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
	}

	IndexCreator createrObj;

	/**
	 * Method to add the given Document to the index
	 * This method should take care of reading the filed values, passing
	 * them through corresponding analyzers and then indexing the results
	 * for each indexable field within the document. 
	 * @param d : The Document to be added
	 * @throws IndexerException : In case any error occurs
	 * @throws TokenizerException 
	 */
	public void addDocument(Document d) throws IndexerException, TokenizerException {

		//		String[] termString=
		//			{d.getField(FieldNames.TITLE)[0],
		//				d.getField(FieldNames.CONTENT)[0],
		//				d.getField(FieldNames.AUTHOR)[0],
		//				d.getField(FieldNames.AUTHORORG)[0],
		//				d.getField(FieldNames.PLACE)[0],
		//				d.getField(FieldNames.CATEGORY)[0]
		//			};
		//		TokenStream termStream=new TokenStream();
		//Need to remove the for loop later.
		//		for(String term : termString)
		//		{
		//			termStream=newToken.consume(term);
		//			if(termStream!=null)
		//				doAnalysisOnStream(termStream);
		//		}

		Tokenizer tokenizeFields=new Tokenizer();
		for(FieldNames fn : FieldNames.values())
		{
			TokenStream termStream=tokenizeFields.consume(d.getField(fn)[0]);
			analyzeAndFiltering(termStream, fn.name());
			createrObj=new IndexCreator(IndexType.CONTENT.name());
			close();
		}
	}
	private void analyzeAndFiltering(TokenStream tStream, String type) throws IndexerException
	{
		AnalyzerFactory factoryObj=AnalyzerFactory.getInstance();
		try
		{
			tStream.reset();
			if(tStream!=null)
			{
				if(type==FieldNames.CONTENT.name())
				{
					Analyzer termAnlzr=factoryObj.getAnalyzerForField(FieldNames.CONTENT, tStream);
					if(termAnlzr!=null)
					{
						while(termAnlzr.increment()){							
						}

					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new IndexerException();
		}


	}

	/**
	 * Method that indicates that all open resources must be closed
	 * and cleaned and that the entire indexing operation has been completed.
	 * @throws IndexerException : In case any error occurs
	 */
	public void close() throws IndexerException, IOException {
		try
		{
//			String SaveIndexDir = System.getProperty("user.dir") + File.separatorChar + "news_training"+ File.separatorChar + "training";
//			FileOutputStream writeIndex =
//					new FileOutputStream();
//			ObjectOutputStream out = new ObjectOutputStream(fileOut);
//			out.writeObject(e);
//			out.close();
//			fileOut.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();	
			throw new IndexerException();
		}
	}
}
