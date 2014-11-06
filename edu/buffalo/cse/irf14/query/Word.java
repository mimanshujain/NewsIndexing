package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Templates;

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
	AnalyzerFactory factoryObj;
	Analyzer termAnlzr;
	TokenStream tStream;
	Map<String, Boolean> wild;
	List<String> wordWild;

	public static Pattern parO = null;
	//	public static Pattern parT = null;
	public static Matcher matchOne = null;
	//	public static Matcher matchTwo = null;

	public Word(String wordVal) {
		wild = new HashMap<String, Boolean>();	
		wordWild = new ArrayList<String>();
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
		generateWildCard();
		postings = null;
		reader = null;
	}

	static 
	{
		parO = Pattern.compile("([a-zA-Z0-9]+)(\\*)([a-zA-Z0-9]+)");
		//		parT = Pattern.compile("([a-zA-Z0-9]?)(\\*)([a-zA-Z0-9]?)");
		matchOne = parO.matcher("");
		//		matchTwo= parT.matcher("");
	}

	private void generateWildCard()
	{
		matchOne.reset(wordVal);
		matchOne = parO.matcher(wordVal);
		
		if(matchOne.matches())
		{
			int count = 0;

				if(count == 0)
				{
					if(!matchOne.group(1).equals(""))
					{
						wild.put(matchOne.group(1), false);
					}
					if(!matchOne.group(3).equals(""))
					{
						wild.put(matchOne.group(3), true);
					}
				}
				else
				{
					if(!matchOne.group(3).equals(""))
					{
						wild.put(matchOne.group(3), true);
					}
				}
			
		}
	}

	public void getAllWildCardTerms(Map<IndexType,IndexReader> fetcherMap)
	{
		getIndexType(fetcherMap);
		if(reader != null)
		{
			Map<String, Integer> dict = new HashMap<String, Integer>();
			dict = reader.getDictionary();
			SortedMap<String, Integer> aTree = new TreeMap<String, Integer>();
			SortedMap<String, Integer> dTree = new TreeMap<String, Integer>(new ReverseComparator());
			SortedMap<String, Integer> tempTree = new TreeMap<String, Integer>();
			
			aTree.putAll(dict);
			dTree.putAll(dict);

			if(wild!= null)
			{
				Iterator<String> itWild = wild.keySet().iterator();

				while(itWild.hasNext())
				{
					String term = itWild.next();
					char last = term.charAt(term.length() - 1);
					int nextValue = (int)last + 1;
					char c = (char)nextValue;
					String lterm = term.substring(0,term.length() - 2)+c;
					boolean val = wild.get(term);
					if(!val)
					{
						tempTree.putAll(aTree.subMap(term, lterm));
					}
					else
					{
						tempTree.putAll(dTree.subMap(term, lterm));
					}
				}
			}
			wordWild.addAll(tempTree.keySet());
		}
	}

	@Override
	public void assignOperands(QueryExpression rightEx, QueryExpression leftEx) {
		// TODO Auto-generated method stub

	}

	@Override
	public String queryInterpretor() {
		return indexType + ":" + originalWord;
	}

	private void setAnalyzer()
	{
		factoryObj = AnalyzerFactory.getInstance();
		termAnlzr = null;
		tStream = new TokenStream();

		if(indexType.equalsIgnoreCase("Term"))
			termAnlzr = factoryObj.getAnalyzerForField(FieldNames.CONTENT, tStream);
		else if(indexType.equalsIgnoreCase("Place"))
			termAnlzr = factoryObj.getAnalyzerForField(FieldNames.PLACE, tStream);
		else if(indexType.equalsIgnoreCase("Author"))
			termAnlzr = factoryObj.getAnalyzerForField(FieldNames.AUTHOR, tStream);


	}

	public Set<String> fetchPostings(Map<IndexType,IndexReader> fetcherMap)
	{
		try
		{
			List<String> allPermutes = getAllWords();

			HashSet<String> setSpace = new HashSet<String>();
			setSpace.add(" ");
			HashSet<String> setFirst = new HashSet<String>(setSpace);

			getIndexType(fetcherMap);
			setAnalyzer();

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
		return wordVal.toString();
	}

	@Override
	public Map<String, Double> getQueryVector(Map<IndexType,IndexReader> fetcherMap) {
		try
		{
			getIndexType(fetcherMap);
			setAnalyzer();

			if(reader != null)
			{
				Map<String, Double> results = new HashMap<String, Double>();
				Map<String, Double> tempResult = null;
				List<String> allPermutes = getAllWords();
				int counter = 1;

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
						tStream.remove();
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

	public class ReverseComparator implements Comparator<String> {
		//		Map<String, Integer> dict;
		//		
		//		public ReverseComparator(Map<String, Integer> dict)
		//		{
		//			this.dict = dict;
		//		}

		@Override
		public int compare(String o1, String o2) {

			StringBuilder sb1 = new StringBuilder(o1);
			StringBuilder sb2 = new StringBuilder(o2);

			o1 = sb1.reverse().toString();
			o2 = sb2.reverse().toString();

			if(o1.compareTo(o2)<0)
				return 1;
			if(o1.compareTo(o2)>0)
				return -1;

			return 0;

		}
	}

	@Override
	public Map <String, List<String>> executeWildCard(Map<IndexType, IndexReader> fetcherMap) {
		if(wild!=null && !wild.isEmpty())
		{
			Map <String, List<String>> wildResult = new HashMap<String, List<String>>();
			generateWildCard();
			if(wordWild.size()>0)
			{
				wildResult.put(wordVal, wordWild);
			}	
			else
				return null;
		}
		return null;
	}
	
	public Set<String> fetchWildPostings(Map<IndexType,IndexReader> fetcherMap)
	{
		Set<String> wildSet = new HashSet<String>();
		if(wild!=null && !wild.isEmpty())
		{
			String tempSaver = wordVal;
			for(String term:wild.keySet())
			{
				wordVal = term;
				wildSet.addAll(fetchPostings(fetcherMap));
			}
			wordVal = tempSaver;
		}
		if(wildSet!=null)
			return wildSet;
		
		return null;
		
	}
}
