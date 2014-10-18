package edu.buffalo.cse.irf14.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Postings implements java.io.Serializable

{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4789969057481055848L;
	Integer totalFreq;
	int collectionFreq;
	int docFreq;
	String termString;
	Map<String, Integer> postingMap;
	private double idf = 0;

	public double getIdf() {
		return idf;
	}

	public Map<String, Integer> getPostingMap() {
		return postingMap;
	}

	public int getCollectionFreq() {
		return collectionFreq;
	}

	List<Integer> position;

	public Postings(String term) 
	{
		totalFreq=0;
		docFreq=0;
		collectionFreq=0;
		postingMap = new HashMap<String,Integer>();
		position=new ArrayList<Integer>();
		termString=term;
	}

	public void setDocID(String docId){

		if (!postingMap.containsKey(docId)) {
			totalFreq =  1;
			collectionFreq++;
			docFreq++;

			postingMap.put(docId, totalFreq);			
		}
		else{
			totalFreq = totalFreq+1;
			collectionFreq++;
			postingMap.put(docId, totalFreq);
		}

	}

	public void calculateIdf(int totalDocCount)
	{
		if(docFreq != 0)
		{
			idf = Math.log10(totalDocCount/docFreq);
		}
	}
}

