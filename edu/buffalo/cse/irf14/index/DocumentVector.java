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

	public DocumentVector() {
		documentVector = new HashMap<String, Map<String,Double>>();
	}

	public void setDocumentVector(TokenStream tStream, String docId) throws IndexerException
	{
		if(tStream != null)
		{
			List<Token> tokenStreamList=tStream.getTokenStreamList();

			for(Token token : tokenStreamList)
			{
				String term = token.toString();
				
				if(term.contains("laser"))
				{
					System.out.println(term);
				}
				
				if(documentVector != null && term != null)
				{
					if(documentVector.containsKey(docId))
					{
						Map<String, Double> vector = documentVector.get(docId);
						if(vector != null)
						{
							double termFreq=1;
							if(vector.containsKey(term))
							{
								termFreq = vector.get(term);
								termFreq = termFreq + 1;								
							}
							updateVector(term, vector, termFreq);
							documentVector.put(docId, vector);
							
							updateMultiWord(term, vector);
						}
					}
					else
					{
						Map<String, Double> vector = new HashMap<String, Double>();
						vector.put(term, 1.0);
						
						updateMultiWord(term, vector);
						
						try {
							documentVector.put(docId, vector);				
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

	private void updateMultiWord(String term, Map<String, Double> vector) {
		String[] strBreakMultiChar = term.split(" ");
		if(strBreakMultiChar.length > 1)
		{
			double subTermFreq = 1;
			for(String str : strBreakMultiChar)
			{
				if(vector.containsKey(str))
				{
					subTermFreq = vector.get(str);
					subTermFreq = subTermFreq + 1;											
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

	private void While(boolean hasNext) {
		// TODO Auto-generated method stub
		
	}

}
