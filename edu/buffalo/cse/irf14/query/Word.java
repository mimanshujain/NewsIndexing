package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;

public class Word implements QueryExpression {

	private String wordVal;
	private String filteredWord;
	private String indexType;
	private Map<String, Integer> postings;
	private IndexReader reader;

	public Word(String wordVal) {

		if(!wordVal.contains(":"))
		{
			indexType = "Term";
			this.wordVal =  wordVal.replaceAll("\"", "");
		}
		else
		{
			int index = wordVal.indexOf(":");
			indexType = wordVal.substring(0,index);
			this.wordVal = wordVal.substring(index+1, wordVal.length());
		}
		postings = null;
		reader = null;
	}

	@Override
	public void assignOperands(QueryExpression rightEx, QueryExpression leftEx) {
		// TODO Auto-generated method stub

	}

	@Override
	public String queryInterpretor() {
		return indexType + ":" + wordVal;
	}

	public Set<String> fetchPostings(Map<IndexType,IndexReader> fetcherMap)
	{
		try
		{
			AnalyzerFactory factoryObj = AnalyzerFactory.getInstance();
			Analyzer termAnlzr = null;
			
			TokenStream tStream = new TokenStream();
			tStream.setTokenStreamList(new Token(wordVal));

			IndexReader reader = null;
			if(IndexType.TERM.name().equalsIgnoreCase(indexType))
			{
				reader = fetcherMap.get(IndexType.TERM);
				termAnlzr = factoryObj.getAnalyzerForField(
						FieldNames.CONTENT, tStream);
			}
			else if(IndexType.CATEGORY.name().equalsIgnoreCase(indexType))
			{
				reader = fetcherMap.get(IndexType.CATEGORY);
			}
			else if(IndexType.AUTHOR.name().equalsIgnoreCase(indexType))
			{
				reader = fetcherMap.get(IndexType.AUTHOR);
				termAnlzr = factoryObj.getAnalyzerForField(
						FieldNames.PLACE, tStream);
			}
			else if(IndexType.PLACE.name().equalsIgnoreCase(indexType))
			{
				reader = fetcherMap.get(IndexType.PLACE);
				termAnlzr = factoryObj.getAnalyzerForField(
						FieldNames.PLACE, tStream);
			}
			
			HashSet<String> setSpace = new HashSet<String>();
			setSpace.add(" ");
			HashSet<String> setFirst = new HashSet<String>(setSpace);
			
			if (termAnlzr != null) {
				while (termAnlzr.increment()) {
				}
			}
			
			if(!tStream.hasNext()) return setFirst;
			
			filteredWord = tStream.next().toString();
			
			List<String> allPermutes = getAllWords();
			
			if(reader != null)
			{
				for(String term : allPermutes)
				{
					postings = reader.getPostings(term);
					if(postings != null)
					{
						setFirst.addAll(new HashSet<String>(postings.keySet()));
					}
				}
				setFirst.removeAll(setSpace);
				return setFirst;
//				
////			HashSet<String> setFirst = null;
//				HashSet<String> setSec = null;
//				HashSet<String> setThird = null;
//				
//				postings = reader.getPostings(filteredWord.toLowerCase());
//				if(postings != null)
//				{
//					setFirst = new HashSet<String>(postings.keySet());
//				}
//				
//				postings = reader.getPostings(CapsFirst(filteredWord));
//				if(postings != null)
//				{
//					setSec = new HashSet<String>(postings.keySet());
//				}
//				
//				postings = reader.getPostings(filteredWord.toUpperCase());
//				if(postings != null)
//				{
//					setThird = new HashSet<String>(postings.keySet());
//				}
//				
//				if(setFirst == null || setSec == null || setThird == null)
//				{
//					setFirst = new HashSet<String>();
//					setSec = new HashSet<String>();
//					setThird = new HashSet<String>();
//					
//					setFirst.addAll(setSpace);
//					setSec.addAll(setSpace);
//					setThird.addAll(setSpace);
//					
//				}
//				
//				setFirst.addAll(setSec);
//				setFirst.addAll(setThird);
//				
////				setSpace.add(" ");
////				
//				setFirst.removeAll(setSpace);
////				
//				return setFirst;
				
//				else if(setFirst != null && setSec == null && setThird == null)
//				{
//					return setFirst;
//				}
//				else if(setFirst == null && setSec != null)
//				{
//					return setSec;
//				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	private List<String> getAllWords()
	{
		List<String> allPermutes = new ArrayList<String>();
		allPermutes.add(filteredWord);
		
		if(!filteredWord.equals(filteredWord.toLowerCase()))
		{
			allPermutes.add(filteredWord.toLowerCase());
		}
		if(!filteredWord.equals(filteredWord.toUpperCase()))
		{
			allPermutes.add(filteredWord.toUpperCase());
		}
		if(!filteredWord.equals(CapsFirst(filteredWord.toLowerCase())))
		{
			allPermutes.add(CapsFirst(filteredWord.toLowerCase()));
		}
		
		return allPermutes;
	}
	
	//Taken from Stackoverflow
	private String CapsFirst(String str) {
	    String[] words = str.split(" ");
	    StringBuilder ret = new StringBuilder();
	    for(int i = 0; i < words.length; i++) {
	        ret.append(Character.toUpperCase(words[i].charAt(0)));
	        ret.append(words[i].substring(1));
	        if(i < words.length - 1) {
	            ret.append(' ');
	        }
	    }
	    return ret.toString();
	}

	public Map<String, Integer> getPostings() {
		return postings;
	}

	@Override
	public String getQueryWords() {
		return wordVal;
	}

	@Override
	public Map<String, Double> getQueryVector(Map<IndexType,IndexReader> fetcherMap) {
		try
		{
			if(IndexType.TERM.name().equalsIgnoreCase(indexType))
			{
				reader = fetcherMap.get(IndexType.TERM);
			}
			if(IndexType.PLACE.name().equalsIgnoreCase(indexType))
			{
				reader = fetcherMap.get(IndexType.PLACE);
			}
			if(IndexType.CATEGORY.name().equalsIgnoreCase(indexType))
			{
				reader = fetcherMap.get(IndexType.CATEGORY);
			}
			if(IndexType.AUTHOR.name().equalsIgnoreCase(indexType))
			{
				reader = fetcherMap.get(IndexType.AUTHOR);
			}
			
			if(reader != null)
			{
				Map<String, Double> results = new HashMap<String, Double>();
				Map<String, Double> tempResult = null;
				List<String> allPermutes = getAllWords();
				int counter = 1;
				
				for(String term : allPermutes)
				{
					if(counter++ == 1)
					{
						tempResult = reader.getTemVector(term , counter);
						if(tempResult != null)
						{
							results.putAll(tempResult);
						}
					}
					else
					{
						tempResult = reader.getTemVector(term , counter/1.2);
						if(tempResult != null)
						{
							results.putAll(tempResult);
						}
					}
				}

				return results;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
