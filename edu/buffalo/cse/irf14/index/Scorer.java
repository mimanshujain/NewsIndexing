package edu.buffalo.cse.irf14.index;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;
import edu.buffalo.cse.irf14.query.Query;

public class Scorer {

	ScoringModel model;
	TreeMap<String, Double> dotProdResult;

	public Scorer(ScoringModel model) {
		this.model = model;
	}

	public TreeMap<String, Double> getOrderedDocuments(Query objQuery, DocumentVector docVector)
	{
		if(model.name() == "TFIDF")
			return relevencyTFIDF(objQuery, docVector);

		else if(model.name() == "OKAPI")
		{
			return relevencyOKAPI(objQuery, docVector);
		}
		return null;
	}

	private TreeMap<String, Double> relevencyTFIDF(Query objQuery, DocumentVector docVector) {

		HashMap<String, Double> result = new HashMap<String, Double>();

		try
		{
			Map<String,Double> queryVector = objQuery.getQueryVector();

			Set<String> docIdSet = objQuery.getDocIdList();

			if(queryVector != null && docIdSet != null && !queryVector.isEmpty() && !docIdSet.isEmpty())
			{
				Iterator<String> docIterator = docIdSet.iterator();

				while(docIterator.hasNext())
				{
					String docId = docIterator.next();
					Map<String, Double> vector = docVector.getDocVector(docId);

					if(vector != null)
					{
						Iterator<String> iterateQuery = queryVector.keySet().iterator();

						while(iterateQuery.hasNext())
						{
							String term = iterateQuery.next();
							double docProdValue = 0.0;
							if(vector.containsKey(term))
							{
								docProdValue = vector.get(term) * queryVector.get(term);
								if(result.containsKey(docId))
								{
									double freq = result.get(docId);
									freq = freq + docProdValue;
									if(freq > 1)
										freq = 1;
									result.put(docId, freq);
								}
								else
								{
									if(docProdValue > 1)
										docProdValue = 1;
									result.put(docId, docProdValue);
								}
							}						 
						}
					}			
				}
			}
			dotProdResult = new TreeMap<String, Double>(new ValueComparator(result));
			dotProdResult.putAll(result);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return dotProdResult;
	}

	private TreeMap<String, Double> relevencyOKAPI(Query objQuery, DocumentVector docVector) {

		HashMap<String, Double> result = new HashMap<String, Double>();
		Map<String,Double> queryVector = objQuery.getQueryVector();
		Set<String> docIdSet = objQuery.getDocIdList();
		double k1 = 1.2, b = 0.75, k3 = 2;
		double avgDocLength = docVector.calculateAvgDocLength();
		
		try 
		{
			if(queryVector != null && docIdSet != null && !queryVector.isEmpty() && !docIdSet.isEmpty())
			{
				double idfSummation = 0.0;

				Iterator<String> docIterator = docIdSet.iterator();

				while(docIterator.hasNext())
				{
					String docId = docIterator.next();
					Map<String, Double> vector = docVector.getUnNormalized(docId);
					int docLength = docVector.getDocLength(docId);
					
					if(vector != null)
					{
						Iterator<String> iterateQuery = queryVector.keySet().iterator();
						while(iterateQuery.hasNext())
						{
							String term = iterateQuery.next();
							double idf = queryVector.get(term);
							double docTermFreq = vector.get(term);
							
							double OKAPIscore = (idf*(1+k1)*docTermFreq)/(k1*((1 - b) + b*(docLength/avgDocLength)) + docTermFreq);
							
							if(objQuery.getQueryTerms().length > 10)
							{
								int termQueryFrq = termFreqQuery(objQuery,term);
								double extraScore = ((k3 + 1)*termQueryFrq)/(k3 + termQueryFrq);
								OKAPIscore = OKAPIscore*extraScore;
							}
							
							result.put(docId, OKAPIscore);
						}
					}
				}
			}
			dotProdResult = new TreeMap<String, Double>(new ValueComparator(result));
			dotProdResult.putAll(result);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	private int termFreqQuery(Query objQuery, String term)
	{
		String[] queryTerms = objQuery.getQueryTerms();
		int counter = 0;

		for(String str : queryTerms)
		{
			if(str.equals(term))
				counter++;
		}

		return counter;
	}

	public class ValueComparator implements Comparator<String> {
		HashMap<String, Double> map;

		public ValueComparator(HashMap<String, Double> map)
		{
			this.map = map;
		}
		@Override
		public int compare(String o1, String o2) {
			double f1 = map.get(o1);
			double f2 = map.get(o2);

			double result = f2 - f1;

			if(result > 0)
				return 1;
			else if (result < 0)
				return -1;
			else
				return o1.compareTo(o2);
		}
	}

}
