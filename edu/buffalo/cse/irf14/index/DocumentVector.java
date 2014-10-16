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
							try {
								vector.put(term, termFreq);				
							}
							catch(NullPointerException e)
							{
								e.printStackTrace();
							}
							documentVector.put(docId, vector);
						}
					}
					else
					{
						Map<String, Double> vector = new HashMap<String, Double>();
						vector.put(term, 1.0);
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

	private void While(boolean hasNext) {
		// TODO Auto-generated method stub
		
	}

}
