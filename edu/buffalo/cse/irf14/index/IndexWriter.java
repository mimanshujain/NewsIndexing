/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
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

		termIndex = new IndexCreator(IndexType.TERM.name());
		authorIndex = new IndexCreator(IndexType.AUTHOR.name());
		categoyIndex = new IndexCreator(IndexType.CATEGORY.name());
		placeIndex = new IndexCreator(IndexType.PLACE.name());

		if (indexDir != null)
			this.indexDir = indexDir;
		else
			this.indexDir = "";
	}

	static int docId;
	static DocumentVector docVector;
	static
	{
		docId = 0;
		docVector = new DocumentVector();
	}
	
	String indexDir;

	IndexCreator termIndex;
	IndexCreator authorIndex;
	IndexCreator categoyIndex;
	IndexCreator placeIndex;
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
	 * @throws IOException
	 */
	public void addDocument(Document d) throws IndexerException {
		docId++;

		Tokenizer tokenizeFields = new Tokenizer();
		Tokenizer tokenizeAuth1 = new Tokenizer(" and ");
		Tokenizer tokenizeAuth2 = new Tokenizer(" AND ");
		
		TokenStream termStream;
		for (FieldNames fn : FieldNames.values()) 
		{
			try {
				if (d.getField(fn) != null && d.getField(fn).length > 0 && fn != FieldNames.FILEID) {
					String str = d.getField(fn)[0];
					
					if (!str.equals(null) && !"".equals(str)) 
					{
						if(fn == FieldNames.AUTHOR)
						{
							if(str.contains(" AND "))
							{
								termStream = tokenizeAuth2.consume(str);
							}
							else if(str.contains(" and "))
							{
								termStream = tokenizeAuth1.consume(str);
							}
							else
							{
								termStream = new TokenStream();
								termStream.setTokenStreamList(new Token(str));
							}
						}
						else
						{
							termStream = tokenizeFields.consume(str);
						}
						analyzeAndFiltering(termStream, fn.name(),
								d.getField(FieldNames.FILEID)[0]);
					}
				}
			} catch (TokenizerException e) {
				e.printStackTrace();
				throw new IndexerException();
			} catch (Exception e) {
				e.printStackTrace();
				throw new IndexerException();
			}
		}
		docVector.normalizeVector(d.getField(FieldNames.FILEID)[0]);
	}

	private void analyzeAndFiltering(TokenStream tStream, String type,
			String fileId) throws IndexerException {
		AnalyzerFactory factoryObj = AnalyzerFactory.getInstance();
		double weight = 1;
		if (factoryObj != null) {
			try {
				if (tStream != null) {
					tStream.reset();
					if (type == FieldNames.CONTENT.name()
							|| type == FieldNames.TITLE.name()) {
						Analyzer termAnlzr = factoryObj.getAnalyzerForField(
								FieldNames.CONTENT, tStream);
						if (termAnlzr != null) {
							while (termAnlzr.increment()) {
							}		
							termIndex.createIndexer(tStream, fileId);
							
						}
					}
					if (type == FieldNames.PLACE.name()) {
						Analyzer termAnlzr = factoryObj.getAnalyzerForField(
								FieldNames.PLACE, tStream);
						if (termAnlzr != null) {
							while (termAnlzr.increment()) {
							}
							placeIndex.createIndexer(tStream, fileId);
						}
					}
					if (type == FieldNames.AUTHOR.name()
							|| type == FieldNames.AUTHORORG.name()) {
						Analyzer termAnlzr = factoryObj.getAnalyzerForField(
								FieldNames.PLACE, tStream);
						if (termAnlzr != null) {
							while (termAnlzr.increment()) {
							}
						}
						authorIndex.createIndexer(tStream, fileId);
					}

					if (type == FieldNames.NEWSDATE.name()) {
						Analyzer termAnlzr = factoryObj.getAnalyzerForField(
								FieldNames.NEWSDATE, tStream);
						if (termAnlzr != null) {
							while (termAnlzr.increment()) {
							}
						}
						termIndex.createIndexer(tStream, fileId);
					}

					if (type == FieldNames.CATEGORY.name()) {
						categoyIndex.createIndexer(tStream, fileId);
					}
					
					if(type.equals(FieldNames.TITLE.name()))
						weight = 4;
					else if(type.equals(FieldNames.AUTHOR.name()) || type.equals(FieldNames.CATEGORY.name())
							|| type.equals(FieldNames.PLACE.name()))
						weight = 100;
					docVector.setDocumentVector(tStream, fileId, weight);
				}
			}

			catch (Exception ex) {

				throw new IndexerException();
			}
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
		
		termIndex.calculateIDF(docId);
		categoyIndex.calculateIDF(docId);
		placeIndex.calculateIDF(docId);
		authorIndex.calculateIDF(docId);
		
		termIndex.setDocVector(docVector);
//		categoyIndex.setDocVector(docVector);
//		placeIndex.setDocVector(docVector);
//		authorIndex.setDocVector(docVector);
		
		writeToDisk(termIndex, IndexType.TERM.name());
		writeToDisk(categoyIndex, IndexType.CATEGORY.name());
		writeToDisk(placeIndex, IndexType.PLACE.name());
		writeToDisk(authorIndex, IndexType.AUTHOR.name());

	}

	private void writeToDisk(IndexCreator objIndex, String diskFileName)
			throws IndexerException {
		try {
			if (!"".equals(indexDir)) {
				List<Integer> indexTermIds = new ArrayList<Integer>(
						termIndex.termDictionary.values());
				Collections.sort(indexTermIds, termIndex.new SortByTermFreq());
				String SaveIndexDir = indexDir + File.separatorChar
						+ diskFileName;

				objIndex.setDocCount();
				FileOutputStream writeIndex = new FileOutputStream(SaveIndexDir);
				GZIPOutputStream zipInput = new GZIPOutputStream(writeIndex);
				ObjectOutputStream indexerOut = new ObjectOutputStream(zipInput);
				indexerOut.writeObject(objIndex);
				indexerOut.writeObject(indexTermIds);
				indexerOut.close();
				writeIndex.close();
			}
		} catch (Exception ex) {
			throw new IndexerException();
		}
	}

}
