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

	private double weight =1;
	private String originalWord;
	private String wordVal;
	private String filteredWord;
	private String indexType;
	private Map<String, Integer> postings;
	private IndexReader reader;

	public Word(String wordVal) {

		originalWord = wordVal;
		if(!wordVal.contains(":"))
		{
			indexType = "Term";
			this.wordVal =  wordVal.replaceAll("\"", "");
		}
		else
		{
			int index = wordVal.indexOf(":");
			indexType = wordVal.substring(0,index);
			String newWord = wordVal.substring(index+1, wordVal.length());
			originalWord = newWord;
			this.wordVal = newWord.replaceAll("\"", "");
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
		return indexType + ":" + originalWord;
	}

	public Set<String> fetchPostings(Map<IndexType,IndexReader> fetcherMap)
	{
		try
		{
			AnalyzerFactory factoryObj = AnalyzerFactory.getInstance();
			Analyzer termAnlzr = null;

			TokenStream tStream = new TokenStream();
			List<String> allPermutes = getAllWords();

			HashSet<String> setSpace = new HashSet<String>();
			setSpace.add(" ");
			HashSet<String> setFirst = new HashSet<String>(setSpace);

			getIndexType(fetcherMap);

			if(indexType.equalsIgnoreCase("Term"))
				termAnlzr = factoryObj.getAnalyzerForField(FieldNames.CONTENT, tStream);
			else if(indexType.equalsIgnoreCase("Place"))
				termAnlzr = factoryObj.getAnalyzerForField(FieldNames.PLACE, tStream);
			else if(indexType.equalsIgnoreCase("Author"))
				termAnlzr = factoryObj.getAnalyzerForField(FieldNames.AUTHOR, tStream);

			if(reader != null)
			{
				for(String term : allPermutes)
				{
					tStream.setTokenStreamList(new Token(term));

					if (termAnlzr != null) {
						
						while (termAnlzr.increment()) {
						}
					}
					if(tStream.hasNext())
					{
						term = tStream.next().toString();
						postings = reader.getPostings(term);
						if(postings != null)
						{
							setFirst.addAll(new HashSet<String>(postings.keySet()));
						}
						tStream.remove();
					}					
				}
			}

			setFirst.removeAll(setSpace);
			return setFirst;
		}
		
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;

	}

	private void getIndexType(Map<IndexType, IndexReader> fetcherMap) {

		if(IndexType.TERM.name().equalsIgnoreCase(indexType))
		{
			reader = fetcherMap.get(IndexType.TERM);
			weight = 1;
		}
		if(IndexType.PLACE.name().equalsIgnoreCase(indexType))
		{
			reader = fetcherMap.get(IndexType.PLACE);
			weight = 3;
		}
		if(IndexType.CATEGORY.name().equalsIgnoreCase(indexType))
		{
			reader = fetcherMap.get(IndexType.CATEGORY);
			weight = 3;
		}
		if(IndexType.AUTHOR.name().equalsIgnoreCase(indexType))
		{
			reader = fetcherMap.get(IndexType.AUTHOR);
			weight = 3;
		}
	}

	private List<String> getAllWords()
	{
		List<String> allPermutes = new ArrayList<String>();
		allPermutes.add(wordVal);

		if(!wordVal.equals(wordVal.toLowerCase()))
		{
			allPermutes.add(wordVal.toLowerCase());
		}
		if(!wordVal.equals(wordVal.toUpperCase()))
		{
			allPermutes.add(wordVal.toUpperCase());
		}
		if(!wordVal.equals(CapsFirst(wordVal.toLowerCase())))
		{
			allPermutes.add(CapsFirst(wordVal.toLowerCase()));
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
			getIndexType(fetcherMap);

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
						tempResult = reader.getTemVector(term , weight * counter);
						if(tempResult != null)
						{
							results.putAll(tempResult);
						}
					}
					else
					{
						tempResult = reader.getTemVector(term , weight * counter * 0.9);
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
