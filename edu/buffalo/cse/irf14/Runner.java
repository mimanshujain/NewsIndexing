/**
 * 
 */
package edu.buffalo.cse.irf14;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.buffalo.cse.irf14.analysis.TokenizerException;
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
		String ipDir = args[0];
		String indexDir = args[1];
//		more? idk!
		//This is taking all the directories in File variable
		File ipDirectory = new File(ipDir);
		//All the sub folders inside the directories
		String[] catDirectories = ipDirectory.list();
		
		String[] files;
		File dir;
		
		Document d = null;
		IndexWriter writer = new IndexWriter(indexDir);
		
		try {
			//Traversing all the sub directories
			for (String cat : catDirectories) {
				//Mapping every folder inside the directory
				dir = new File(ipDir+ File.separator+ cat);
				//Taking all the files in files variables
				files = dir.list();
				
				if (files == null)
					continue;
				
				for (String f : files) {
					try {
						d.setField(FieldNames.FILEID, f);
						d.setField(FieldNames.CATEGORY, cat);	
						
						d = Parser.parse(dir.getAbsolutePath() + File.separator +f);
						if(d==null)
							throw new ParserException();

						writeToFile(d);
						writer.addDocument(d);
					} catch (ParserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					catch(TokenizerException tEx)
					{
						tEx.printStackTrace();
					}
				}
				
			}
			
			writer.close();
		} catch (IndexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void writeToFile(Document d)
	{
		try 
		{
			File saveData=new File("E:"+File.separator+"Dropbox"+File.separator+"Master"+File.separator+"Results.txt");
			FileWriter fw;
			
			fw = new FileWriter(saveData.getAbsoluteFile(),true);
			BufferedWriter bw=new BufferedWriter(fw);
			
			if(!saveData.exists())
			{
				saveData.createNewFile();
			}
			bw.write(d.toString());
			bw.newLine();bw.newLine();
			bw.close();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
