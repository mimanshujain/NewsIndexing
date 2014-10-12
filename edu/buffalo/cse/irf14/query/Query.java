package edu.buffalo.cse.irf14.query;

/**
 * Class that represents a parsed query
 * @author nikhillo
 *
 */
public class Query {
	
	String strRep="";
	
	public Query(String strRep)
	{
		this.strRep=strRep;
	}
	
	/**
	 * Method to convert given parsed query into string
	 */
	public String toString() {
		return strRep;
	}
}
