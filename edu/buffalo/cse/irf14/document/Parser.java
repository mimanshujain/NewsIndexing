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
	private static Matcher matchFullAuth = null;
	private static Matcher matchAuth = null;
	private static Matcher matchPlace = null;

	private static BufferedReader readFile;
	static List<String> regexValues = new ArrayList<String>();
	static {
		regexValues.add("(?i:\\<AUTHOR\\>)(\\s+\\w+\\s+)(.*)(,)(\\s+)(\\w+)");
		regexValues.add("(?i:\\<AUTHOR\\>\\s+)(?i:by\\s+)(\\w.*)(\\<)");
		regexValues.add("(.*)(,\\s+)(\\w+\\s+\\d{1,2})(\\s+\\-\\s+)(.*)");

		matchFullAuth = Pattern.compile(regexValues.get(0)).matcher("");
		matchAuth = Pattern.compile(regexValues.get(1)).matcher("");
		matchPlace = Pattern.compile(regexValues.get(2)).matcher("");
	}

	public Parser() {
	}

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
			if (filename == null || filename.equals("")) {
				throw new ParserException();
			}
			File parsingOp = new File(filename);

			InputStream openStream = new FileInputStream(parsingOp);

			readFile = new BufferedReader(new InputStreamReader(openStream));

			int count = 0, authorFlag = 0, placeFlag = 0, contentFlag = 0;
			String contentStart = "";

			StringBuilder sbContent = new StringBuilder();
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
						docParser.setField(FieldNames.TITLE, trimText);
						count++;
					} else if (contentFlag == 0) {
						if (authorFlag == 0
								&& (parseMatch = checkAuthor.matcher(trimText))
										.find()) {
							count = 1;
							matchFullAuth = matchFullAuth.reset(trimText);
							matchAuth = matchAuth.reset(trimText);

							if (matchFullAuth.find()) {
								docParser.setField(FieldNames.AUTHOR,
										matchFullAuth.group(2));
								docParser.setField(FieldNames.AUTHORORG,
										matchFullAuth.group(5));
								authorFlag = 1;
							}

							else if (matchAuth.find()) {
								docParser.setField(FieldNames.AUTHOR,
										matchAuth.group(1));
								authorFlag = 1;
							}
						} else {
							matchPlace = matchPlace.reset(trimText);
							if (placeFlag == 0 && matchPlace.find()) {

								docParser.setField(FieldNames.PLACE,
										matchPlace.group(1));
								docParser.setField(FieldNames.NEWSDATE,
										matchPlace.group(3));
								if (sbContent.toString().equals(""))
									sbContent.append(contentStart);

								placeFlag = 1;

							} else if (placeFlag == 1 && authorFlag == 1) {
								contentFlag = 1;

								if (!sbContent.toString().equals("")) {
									sbContent.append(" " + trimText);
								} else {
									sbContent.append(trimText);
								}

							} else {
								if (!sbContent.toString().equals("")) {
									sbContent.append(" " + trimText);
								} else {
									sbContent.append(trimText);
								}

							}
						}

					} else if (contentFlag == 1) {

						if (!sbContent.toString().equals("")) {
							sbContent.append(" " + trimText);
						} else {
							sbContent.append(trimText);
						}
					}
				}
				lineValue = readFile.readLine();
			}

			docParser.setField(FieldNames.CONTENT, sbContent.toString());

		}

		catch (Exception ex) {
			throw new ParserException();
		}
		return docParser;
	}

}
