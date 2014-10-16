/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author nikhillo
 * Class that emulates reading data back from a written index
 */
public class IndexReader {
	/**
	 * Default constructor
	 * @param indexDir : The root directory from which the index is to be read.
	 * This will be exactly the same directory as passed on IndexWriter. In case 
	 * you make subdirectories etc., you will have to handle it accordingly.
	 * @param type The {@link IndexType} to read from
	 */
	public IndexReader(String indexDir, IndexType type) {

		if (indexDir!=null)
			this.indexDir=indexDir;
		else
			this.indexDir="";

		indexType=type.name();
		objCreator=readFromDisk();
		//		lst=new ArrayList<Integer>;
	}
	String indexDir;
	String indexType;
	IndexCreator objCreator;
	List<Integer> orderedTerms;
	/**
	 * Get total number of terms from the "key" dictionary associated with this 
	 * index. A postings list is always created against the "key" dictionary
	 * @return The total number of terms
	 */
	public int getTotalKeyTerms() {

		try
		{
			if(objCreator!=null)
			{
				return objCreator.getTotalTerms();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return -1;
	}

	/**
	 * Get total number of terms from the "value" dictionary associated with this 
	 * index. A postings list is always created with the "value" dictionary
	 * @return The total number of terms
	 */
	public int getTotalValueTerms() {

		try{
			return objCreator.getDocCount();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return -1;
	}

	/**
	 * Method to get the postings for a given term. You can assume that
	 * the raw string that is used to query would be passed through the same
	 * Analyzer as the original field would have been.
	 * @param term : The "analyzed" term to get postings for
	 * @return A Map containing the corresponding fileid as the key and the 
	 * number of occurrences as values if the given term was found, null otherwise.
	 */
	public Map<String, Integer> getPostings(String term) {
		try
		{
			Map<String, Integer> m = null;
			if(objCreator!=null)
			{
				m=objCreator.getTermDictionary(term);
				if(m!=null)  return new HashMap<String, Integer>(m);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Method to get the top k terms from the index in terms of the total number
	 * of occurrences.
	 * @param k : The number of terms to fetch
	 * @return : An ordered list of results. Must be <=k fr valid k values
	 * null for invalid k values
	 */
	public List<String> getTopK(int k) {
		try
		{
			if (k <= 0)
				return null;
			List<String> terms=new ArrayList<String>(); 
			int i=0;

			if(orderedTerms!=null && orderedTerms.size()>0)
			{
				for(int termId : orderedTerms)
				{
					if(i<k)
					{
						Postings p = objCreator.termPostings.get(termId);
						terms.add(p.termString);
					}
					else
						break;
					i++;
				}


				return terms;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}


	private IndexCreator readFromDisk() 
	{
		try
		{
			if(!"".equals(indexDir))
			{
				String ReadIndexDir = indexDir+File.separatorChar + indexType;
				FileInputStream readIndex =
						new FileInputStream(ReadIndexDir);
				GZIPInputStream  unzipOut = new GZIPInputStream(readIndex);
				ObjectInputStream  indexerIn = new ObjectInputStream(unzipOut);
				objCreator = (IndexCreator) indexerIn.readObject();
				orderedTerms = (List<Integer>)indexerIn.readObject();
				indexerIn.close();
				return objCreator;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();	
		}

		return null;
	}

	public DocumentVector getDocVector() {
		return objCreator.getDocVector();
	}
	
	/**
	 * Method to implement a simple boolean AND query on the given index
	 * @param terms The ordered set of terms to AND, similar to getPostings()
	 * the terms would be passed through the necessary Analyzer.
	 * @return A Map (if all terms are found) containing FileId as the key 
	 * and number of occurrences as the value, the number of occurrences 
	 * would be the sum of occurrences for each participating term. return null
	 * if the given term list returns no results
	 * BONUS ONLY
	 */
	public Map<String, Integer> query(String...terms) {

		Map<String, Integer> o=null;
		if(terms!=null && terms.length>0)
		{
			o = getPostings(terms[0]);
			if(o!=null)
			{
				for (int i = 1; i < terms.length;i++) {
					Map<String, Integer> oo = getPostings(terms[i]);
					if(oo!=null)
					{
						o.keySet().retainAll(oo.keySet());
						for (Iterator<String> s = o.keySet().iterator(); s.hasNext(); ) {
							String ss = s.next();
							Integer f = o.get(ss) + oo.get(ss);
							o.put(ss, f);
						}
					}
				}
			}
			if(o.size()==0) return null;
		}
		return o;
	}

	public Map<Integer, Double> getTemVector(String term)
	{
		return objCreator.getTemVector(term);
	}

}
