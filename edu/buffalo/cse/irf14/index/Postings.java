package edu.buffalo.cse.irf14.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Postings {

	Integer totalFreq;
	int collectionFreq;
	int docFreq;
	Map<Integer, Integer> postingMap;
	List<Integer> position;

	public Postings() 
	{
		totalFreq=0;
		docFreq=0;
		collectionFreq=0;
		postingMap = new HashMap<Integer,Integer>();
		position=new ArrayList<Integer>();
	}

	public void setDocID(int docId){

		if (!postingMap.containsKey(docId)) {
			totalFreq =  1;
			collectionFreq++;
			docFreq++;
			
			postingMap.put(docId, (int)totalFreq);			
		}
		else{
			totalFreq = totalFreq+1;
			collectionFreq++;
			postingMap.put(docId, totalFreq);
		}

	}

//	//Calculating Total Frequency
//	public int getCollectionFreq()
//	{
//		collectionFreq=0;
//		if(postingMap!=null)
//		{
//			if(postingMap.size()>0)
//			{
//				for(String str: postingMap.keySet())
//				{
//					collectionFreq=collectionFreq+postingMap.get(str);
//				}
//			}
//			else
//				return 0;
//		}
//
//		return collectionFreq;
//	}
//
//	//To get Doc Freq
//	public int getDocFrequency()
//	{
//		docFreq=0;
//		if(postingMap!=null)
//		{
//			if(postingMap.size()>0)
//			{
//				docFreq=postingMap.size();
//			}
//		}
//		return docFreq;
//	}
	//	public void printVal(){
	//		System.out.println(postingMap.values());
	//	}
	//	public Collection<Integer> getPostingsMap(){
	//		return postingMap.values();
	//	}
	//	
	//	
}

//	public static void main(String[] args){
//		//		Postings postings = new Postings();
//		//		postings.setDocID("Apple");
//		//		postings.setDocID("Banana");
//		//		postings.setDocID("Apple");
//		//		postings.setDocID("Apple");
//		//		postings.setDocID("Banana");
//		//		postings.setDocID("Apple");
//		//		postings.setDocID("Apple");
//		//		postings.setDocID("Banana");
//		//		postings.setDocID("Apple");
//		//		postings.setDocID("Apple");
//		//		postings.setDocID("Banana");
//		//		postings.setDocID("Apple");
//		//		System.out.println(postings.getPostingsMap());
//		//		
//
//		String key="Apple";	
//
//		testMap(key);
//		key="Apple";
//		testMap(key);
//		key="Apple";
//		testMap(key);
//		key="Banana";
//		testMap(key);
//		key="Banana";
//		testMap(key);
//		key="Anar";
//		testMap(key);
//		key="Banana";
//		testMap(key);
//		System.out.println(mapPost.values());
//		//		int i=0;
//		//		while(i<mapPost.size())
//		//		{
//		//			System.out.println();
//		//		}
//
//		for(String str:mapPost.keySet())
//		{
//			Postings p=mapPost.get(str);
//			p.printVal();
//
//		}
//
//		//		Postings p1 = new Postings();
//		//		p1.setDocID("Apple");
//		//		mapPost.put(termId1, p1);
//		//		String key="Apple";
//		//		p1 = testMap(mapPost, p1);
//		//		Postings p2 = new Postings();
//		//		p1.setDocID("Banana");
//		//		mapPost.put(termId1, p2);
//		//		mapPost.put(termId1, p2);
//		//		mapPost.put(termId1, p2);
//		//		mapPost.put(termId1, p2);
//	}
//	private static void testMap(String term) {
//		Postings p;
//		String key=testDict(term);
//		if(key!=null)
//		{
//			if(mapPost.containsKey(key))
//			{
//				p=mapPost.get(key);		
//				p.setDocID(term);
//			}
//			else
//			{
//				//String termId1=String.valueOf(++termId);
//				p=new Postings();
//				p.setDocID(term);
//				mapPost.put(key, p);
//			}
//		}
//
//	}
//
//	private static String testDict(String term)
//	{
//		String termId1;
//		if(dict.containsKey(term))
//		{
//			termId1=dict.get(term);
//			return termId1;
//		}
//		else
//		{
//			dict.put(term,String.valueOf(++termId));
//			return String.valueOf(termId);
//		}
//			
//					
//	}
//}
