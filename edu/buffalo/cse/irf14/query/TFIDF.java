package edu.buffalo.cse.irf14.query;

public class TFIDF {
	private int termFreq;
	private int docFreq;
	private int totalDocCount;
	private double weightedTermFreq;
	private double invertedDocumentFreqwoLog;
	private double invertedDocumentFreq;
	private double TFIDF;
	
	public TFIDF(int termFreq, int docFreq, int totalDocCount){
		this.termFreq = termFreq;
		this.docFreq= docFreq;
		this.totalDocCount= totalDocCount;
		
	}
	public int getTermFreq(){
		return termFreq;
	}
	public int getDocFreq(){
		return docFreq;
	}
	public int getTotalDocCount(){
		return totalDocCount;
	}
	
	
	public double TFIDFCalculator(){
		weightedTermFreq= 1+ Math.log10(termFreq);
		invertedDocumentFreqwoLog = totalDocCount/docFreq;
		invertedDocumentFreq = Math.log10(invertedDocumentFreqwoLog);
		TFIDF = weightedTermFreq*invertedDocumentFreq;
		
		
		return TFIDF;
	}	
public static void main(String[] args){
	TFIDF tfidf = new TFIDF(1000, 100, 1000000);
	System.out.println("The TFIDF = :- "+tfidf.TFIDFCalculator());
}
}
