/**
 * 
 */
package edu.buffalo.cse.irf14;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.IndexWriter;
import edu.buffalo.cse.irf14.index.IndexerException;

/**
 * @author nikhillo
 *
 */
public class Runner {

	/**
	 * 
	 */
	public Runner() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long lStartTime = System.currentTimeMillis();
		
		String ipDir = args[0];
		String indexDir = args[1];
		//This is taking all the directories in File variable
		File ipDirectory = new File(ipDir);
		//All the sub folders inside the directories
		String[] catDirectories = ipDirectory.list();

		String[] files;
		File dir;

		Document d = null;
		IndexWriter writer = new IndexWriter(indexDir);
		
		String docId="";
		
		try {
//			//Traversing all the sub directories
//			for (String cat : catDirectories) {
//				//Mapping every folder inside the directory
//				dir = new File(ipDir+ File.separator+ cat);
//				//Taking all the files in files variables
//				files = dir.list();
//
//				if (files == null)
//					continue;
//				
//				for (String f : files) {
//					try {
//
//						d = Parser.parse(dir.getAbsolutePath() + File.separator +f);
//						if (d != null)
//						{
//							//writeToFile(d);
//							docId=d.getField(FieldNames.FILEID)[0];
//							writer.addDocument(d);
//						}
//
//						if(d==null)
//							throw new ParserException();
//
//					} 
//					catch (ParserException e) {
//						System.out.println("Inside Runner Parser: "+docId);
//						
//					} 
//				}
//
//			}
//
//			writer.close();
			PrintStream stream = new PrintStream(new File(indexDir+ File.separator+ "ScoreResult"));
			String userQuery = "NATO";
			SearchRunner searcher = new SearchRunner(indexDir, ipDir, 'Q', stream);
			searcher.query(userQuery, ScoringModel.TFIDF);
			
			long lEndTime = System.currentTimeMillis();
			long difference = lEndTime - lStartTime;
			 
			System.out.println("Elapsed milliseconds: " + difference);
			
		} 
//		catch (IndexerException e) {
//
//		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
