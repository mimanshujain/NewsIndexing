/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import java.util.Properties;

/**
 * @author nikhillo Class that parses a given file into a Document
 */
public class Parser {

	public static Pattern parsePattern = null;
	public static Matcher parseMatch = null;
	private static Matcher matchFullAuth=null;
	private static Matcher matchAuth=null;
	private static Matcher matchPlace=null;

	private static BufferedReader readFile;
	static List<String> regexValues = new ArrayList<String>();
	static {
		regexValues.add("(?i:\\<AUTHOR\\>)(\\s+\\w+\\s+)(.*)(,)(\\s+)(\\w+)");
		regexValues.add("(?i:\\<AUTHOR\\>\\s+)(?i:by\\s+)(\\w.*)(\\<)");
		regexValues.add("(.*)(,\\s+)(\\w+\\s+\\d{1,2})(\\s+\\-\\s+)(.*)");
		//regexValues.add("(.*)(\\s+\\-\\s+)(.*)");

		matchFullAuth=Pattern.compile(regexValues.get(0)).matcher("");
		matchAuth=Pattern.compile(regexValues.get(1)).matcher("");
		matchPlace=Pattern.compile(regexValues.get(2)).matcher("");
	}
	
	public Parser() {
	}

	//	private static String regFullAuth="(?i:\\<AUTHOR\\>)(\\s+\\w+\\s+)(.*)(,)(\\s+)(\\w+)";
	//	private static String regAuth="(?i:\\<AUTHOR\\>\\s+)(?i:by\\s+)(\\w.*)(\\<)";
	//	private static String 
	/**
	 * Static method to parse the given file into the Document object
	 * 
	 * @param filename
	 *            : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException
	 *             In case any error occurs during parsing
	 */
	public static Document parse(String filename) throws ParserException {
		Document docParser = null;
		try {
			if (filename == null || filename.equals(""))
				throw new ParserException();
			File parsingOp = new File(filename);

			InputStream openStream = new FileInputStream(parsingOp);

			readFile = new BufferedReader(new InputStreamReader(openStream));			

			//			List<String> regexValues = new ArrayList<String>();
			//			regexValues.add("(?i:\\<AUTHOR\\>)(\\s+\\w+\\s+)(.*)(,)(\\s+)(\\w+)");
			//			regexValues.add("(?i:\\<AUTHOR\\>\\s+)(?i:by\\s+)(\\w.*)(\\<)");
			//			regexValues.add("(.*)(,\\s+)(\\w+\\s+\\d{1,2})(\\s+\\-\\s+)(.*)");
			//			regexValues.add("(\\d+(?:\\.\\d+)?)");
			//			regexValues.add("(.*)(\\s+\\-\\s+)(.*)");

			int count = 0, authorFlag = 0, placeFlag = 0, contentFlag = 0;
			String contentStart = "";

			StringBuilder sbContent=new StringBuilder();
			String lineValue = readFile.readLine();

			Pattern checkAuthor = Pattern.compile("AUTHOR",
					Pattern.CASE_INSENSITIVE);
			docParser = new Document();
			docParser.setField(FieldNames.FILEID, parsingOp.getName());
			docParser.setField(FieldNames.CATEGORY, parsingOp.getParentFile()
					.getName());
			while (lineValue != null) {
				if (!lineValue.isEmpty()) {
					String trimText = lineValue.trim();
					if (count == 0) {
						//						parsePattern = Pattern.compile((String) regexValues
						//								.get(3));
						//						parseMatch = parsePattern.matcher(trimText);
						docParser.setField(FieldNames.TITLE, trimText);
						count++;
					} else if (contentFlag == 0) {
						if (authorFlag == 0 && (parseMatch = checkAuthor.matcher(trimText)).find()) 
						{
							count = 1;
//							Iterator<String> travRegex = regexValues.iterator();
							
							matchFullAuth=matchFullAuth.reset(trimText);
							matchAuth=matchAuth.reset(trimText);
							
//							while (travRegex.hasNext()) {
//								parsePattern = Pattern
//										.compile((String) travRegex.next());
//								parseMatch.reset();
//								parseMatch = parsePattern.matcher(trimText);
								if (matchFullAuth.find()) //parseMatch.find() count == 1 && 
								{
									docParser.setField(FieldNames.AUTHOR,
											matchFullAuth.group(2));
									docParser.setField(FieldNames.AUTHORORG,
											matchFullAuth.group(5));
									authorFlag = 1;
//									break;
								} 
								
								else if (matchAuth.find()) //parseMatch.find() count == 2 && 
								{
									docParser.setField(FieldNames.AUTHOR,
											matchAuth.group(2));
									authorFlag = 1;
//									break;
								} 
//								else if (count > 2)
//									break;
//								count++;
//							}
						} 
						else {
							//							parsePattern = Pattern.compile((String) regexValues.get(2));
							//							parseMatch.reset();
							matchPlace = matchPlace.reset(trimText);
						if (placeFlag == 0 && matchPlace.find()) //(parseMatch = parsePattern.matcher(trimText)).find()
						{
							//if ((parseMatch = parsePattern.matcher(trimText)).find()) {
							docParser.setField(FieldNames.PLACE, matchPlace.group(1)); //parseMatch.group(1)
							docParser.setField(FieldNames.NEWSDATE, matchPlace.group(3)); //parseMatch.group(3)
							contentStart = matchPlace.group(5);//parseMatch.group(5);
							//									contentDump.add(contentStart);
							if(sbContent.toString().equals(""))
								sbContent.append(contentStart);
							//									contentNumber.addAll(findNumber(
							//											contentStart,
							//											(String) regexValues.get(3)));

							placeFlag = 1;
							//}
						} else if (placeFlag == 1 && authorFlag == 1) {
							contentFlag = 1;
							//contentDump.add(trimText);
							if(!sbContent.toString().equals(""))
							{
								sbContent.append(" "+trimText);
							}
							else
							{
								sbContent.append(trimText);
							}
							//								contentNumber.addAll(findNumber(trimText,
							//										(String) regexValues.get(3)));
						} else {
							//								contentDump.add(trimText);
							if(!sbContent.toString().equals(""))
							{
								sbContent.append(" "+trimText);
							}
							else
							{
								sbContent.append(trimText);
							}
							//								contentNumber.addAll(findNumber(trimText,
							//										(String) regexValues.get(3)));
						}
						}

					} else if (contentFlag == 1) {
						//						contentDump.add(trimText);
						if(!sbContent.toString().equals(""))
						{
							sbContent.append(" "+trimText);
						}
						else
						{
							sbContent.append(trimText);
						}
					}
				}
				lineValue = readFile.readLine();
			}
			//
			//			docParser.setField(FieldNames.NUMBERS,
			//					contentNumber.toArray(new String[contentNumber.size()]));

			docParser.setField(FieldNames.CONTENT,
					sbContent.toString());
			//System.out.println("Parser Ended");

		}

		catch (Exception ex) {
			ex.printStackTrace();
			throw new ParserException();
		}
		return docParser;
	}

	//	private static List<String> findNumber(final String matchData,
	//			final String regex) {
	//		parsePattern = Pattern.compile(regex);
	//		parseMatch = parsePattern.matcher(matchData);
	//		List<String> matchedData = new ArrayList<String>();
	//		while (true) {
	//			if (parseMatch.find()) {
	//				matchedData.add((String) parseMatch.group(1));
	//			} else
	//				break;
	//		}
	//		return matchedData;
	//	}

}
