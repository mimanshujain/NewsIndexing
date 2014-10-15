package edu.buffalo.cse.irf14.query;

public class Okapi {
	private TFIDF tfidf;
	private int termFreq;
	private int docFreq;
	private int totalDocCount;
	private int lengthOfDocument;
	private int avgLengthOfDocumentinCorpus;
	private double idf;
	private double score;
	private double okapi;
	private double b = 0.75;
	private double k1 = 1.2;

	public Okapi(int lengthOfDocument, int avgLengthOfDocumentinCorpus,
			TFIDF tfidf) {
		this.tfidf = tfidf;
		this.termFreq = tfidf.getTermFreq();

		this.docFreq = tfidf.getDocFreq();

		this.totalDocCount = tfidf.getTotalDocCount();

		this.avgLengthOfDocumentinCorpus = avgLengthOfDocumentinCorpus;

		this.lengthOfDocument = lengthOfDocument;

	}

	public double OkapiCalculator() {
		idf = Math.log10((totalDocCount - docFreq + 0.5) / (docFreq + 0.5));

		score = (termFreq * (k1 + 1))
				/ (termFreq + (k1 * (1 - b + (b * (lengthOfDocument / avgLengthOfDocumentinCorpus)))));

		return score;
	}

	public static void main(String[] args) {
		Okapi okapi = new Okapi(10000, 100000, new TFIDF(1000, 100, 1000000));
		double score = okapi.OkapiCalculator();

		System.out.println(score);
	}

}
