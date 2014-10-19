package edu.buffalo.cse.irf14.index;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;

public class DocumentVector implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map<String, Map<String,Double>> documentVector;
	Map<String, Map<String,Double>> unNormalized;
	Map<String, Integer> docLength;
	double avgDocLen = 0.0;
	
	public DocumentVector() {
		documentVector = new HashMap<String, Map<String,Double>>();
		unNormalized = new HashMap<String, Map<String,Double>>();	
		docLength = new HashMap<String, Integer>();
	}

	public void setDocumentVector(TokenStream tStream, String docId, double weight) throws IndexerException
	{
		if(tStream != null)
		{
			List<Token> tokenStreamList=tStream.getTokenStreamList();

			for(Token token : tokenStreamList)
			{
				String term = token.toString();
				
				if(documentVector != null && term != null)
				{
					if(documentVector.containsKey(docId))
					{
						Map<String, Double> vector = documentVector.get(docId);
						Map<String, Double> secVec = unNormalized.get(docId);
						
						if(vector != null)
						{
							double freq = 1;
							double termFreq=weight;
							if(vector.containsKey(term))
							{
								freq = secVec.get(term);
								freq = freq + 1;
								termFreq = vector.get(term);
								termFreq = termFreq + weight;								
							}
							updateVector(term, vector, termFreq);
							updateVector(term, secVec, freq);														
							
							updateMultiWord(term, vector, weight);
							updateMultiWord(term, secVec, freq);
							
							documentVector.put(docId, vector);
							unNormalized.put(docId, secVec);
						}
						
						int length = docLength.get(docId);
						String[] strBreak = term.split(" ");
						length = length + strBreak.length;
						docLength.put(docId, length);
						
					}
					else
					{
						Map<String, Double> vector = new HashMap<String, Double>();
						Map<String, Double> secVec = new HashMap<String, Double>();
						
						vector.put(term, 1.0);
						secVec.put(term, 1.0);
						
						updateMultiWord(term, vector, weight);
						updateMultiWord(term, secVec, 1);
						
						try {
							documentVector.put(docId, vector);		
							unNormalized.put(docId, secVec);
							
							String[] strBreak = term.split(" ");
							int length = strBreak.length;
							docLength.put(docId, length);
						}
						catch(NullPointerException e)
						{
							e.printStackTrace();
						}						
					}
				}
			}					
		}
	}

	private void updateMultiWord(String term, Map<String, Double> vector, double weight) {
		String[] strBreakMultiChar = term.split(" ");
		if(strBreakMultiChar.length > 1)
		{
			double subTermFreq = weight;
			for(String str : strBreakMultiChar)
			{
				if(vector.containsKey(str))
				{
					subTermFreq = vector.get(str);
					subTermFreq = subTermFreq + weight;											
				}								
				updateVector(str, vector, subTermFreq);
			}
		}
	}

	private void updateVector(String term, Map<String, Double> vector,
			double termFreq) {
		try {
			vector.put(term, termFreq);				
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
		}
	}
	
	public void normalizeVector(String docId) throws IndexerException
	{
		if(documentVector != null )
		{
			Map<String, Double> vector = documentVector.get(docId);
			Iterator<Double> it = vector.values().iterator();
			double squareTotal = 0.0;
			
			while(it.hasNext()){
				squareTotal = squareTotal + Math.pow(it.next(), 2);
			}
			Iterator<String> normalizeIt = vector.keySet().iterator();
			
			double rootOfTotal = Math.sqrt(squareTotal);
			
			while(normalizeIt.hasNext())
			{
				String key = normalizeIt.next();
				if(vector.containsKey(key))
				{
					vector.put(key, (vector.get(key)/rootOfTotal));
				}
			}
		}
	}
	
	public Map<String, Double>getDocVector(String docId)
	{
		if(documentVector.containsKey(docId))
		{
			return documentVector.get(docId);
		}
		else
			return null;
	}

	public Map<String, Double> getUnNormalized(String docId) {
		if(unNormalized.containsKey(docId))
		{
			return unNormalized.get(docId);
		}
		else
			return null;
	}

	public int getDocLength(String docId) {

		if(docLength.containsKey(docId))
		{
			return docLength.get(docId);
		}
		return 0;
	}
	
	public double calculateAvgDocLength()
	{
		double length = 0.0;
		if(docLength != null && !docLength.isEmpty())
		{
			Iterator<String> docIt = docLength.keySet().iterator();

			while(docIt.hasNext())
			{
				String docId = docIt.next();
				length = length + docLength.get(docId);			
			}
		}
		return length/docLength.size();
	}
	
	private void While(boolean hasNext) {
		// TODO Auto-generated method stub
		
	}

}
