/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author nikhillo
 * Wrapper class that holds {@link FieldNames} to value mapping
 */
public class Document {
	//Sample implementation - you can change this if you like
	private HashMap<FieldNames, String[]> map;
	
	/**
	 * Default constructor
	 */
	public Document() {
		map = new HashMap<FieldNames, String[]>();
		for(FieldNames fn : map.keySet())
		{
			setField(fn, "");
		}
	}
	
	/**
	 * Method to set the field value for the given {@link FieldNames} field
	 * @param fn : The {@link FieldNames} to be set
	 * @param o : The value to be set to
	 */
	public void setField(FieldNames fn, String... o) {
		map.put(fn, o);
	}
	
	/**
	 * Method to get the field value for a given {@link FieldNames} field
	 * @param fn : The field name to query
	 * @return The associated value, null if not found
	 */
	public String[] getField(FieldNames fn) {		
		return map.get(fn);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb=new StringBuilder();
//		Iterator it=map.entrySet().iterator();
//		while(it.hasNext())
//		{
//			sb.append(map.get(it.next()));
//		}
//		
		for(FieldNames fn : map.keySet())
		{
			if (map.get(fn).length>0)
			{
				sb.append(map.get(fn)[0]);
			}
		}
//		for(int i=0;i<map.size();i++)
//		{
//			sb.append(map.get(fn.));
//		}
		//sb.append(map.keySet().toString());
		return sb.toString();
	}
}
