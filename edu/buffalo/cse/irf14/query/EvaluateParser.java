package edu.buffalo.cse.irf14.query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.irf14.document.ParserException;

public class EvaluateParser {

	private static BufferedReader readFile;
	public static Pattern parsePattern = null;
	private static Matcher match = null;
	private static String qregex;
	public static int numQ;
	public static String[] qid;
	public static String[] query;

	public EvaluateParser() {

	}
	static
	{
		qregex ="(.*)(\\:\\{)(.*)(\\})";
		parsePattern = Pattern.compile(qregex);
		//		match = 
		match = parsePattern.matcher("");
	}
	public static void parseQueries(File queryFile) throws QueryParserException
	{
		try {
			if (queryFile == null || queryFile.equals("")) {

				throw new QueryParserException();
			}

			File parsingOp = queryFile;

			InputStream openStream = new FileInputStream(parsingOp);

			readFile = new BufferedReader(new InputStreamReader(openStream));
			//			StringBuilder sbContent = new StringBuilder();
			String lineValue = readFile.readLine();

			int count = 0;
			while (lineValue != null) {
				String text = lineValue.trim();
				if(lineValue.trim().contains("numQueries="))
				{
					int len = lineValue.trim().lastIndexOf("=");
					String numb = text.substring(len+1);
					numQ = Integer.parseInt(numb);
					qid = new String[numQ];
					query = new String[numQ];
				}
				else if(!lineValue.trim().isEmpty())
				{
					match = parsePattern.matcher("");
					match.reset(text);
					if(match.matches())
					{
						qid[count]= match.group(1);
						query [count]= match.group(3);	
						count ++;
					}
				}
				lineValue = readFile.readLine();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
