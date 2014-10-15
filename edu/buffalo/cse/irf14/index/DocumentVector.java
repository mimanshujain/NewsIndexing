package edu.buffalo.cse.irf14.index;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;

public class DocumentVector {

	Map<String, Map<String,Integer>> documentVector;

	public DocumentVector() {
		documentVector = new HashMap<String, Map<String,Integer>>();
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
						Map<String, Integer> vector = documentVector.get(docId);
						if(vector != null)
						{
							int termFreq=1;
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
						Map<String, Integer> vector = new HashMap<String, Integer>();
						vector.put(term, 1);
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

}
