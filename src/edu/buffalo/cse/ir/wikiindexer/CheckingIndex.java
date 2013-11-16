package edu.buffalo.cse.ir.wikiindexer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;
import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.indexer.IndexReader;

public class CheckingIndex {

	public Map<String, LinkedList<Integer[]>> invIndex;
	public Map<Integer, LinkedList<Integer>> fwdIndex;
	
	public int getTotalKeyTerms(String fileName, boolean isForward) {
		//TODO: Implement this method
		FileInputStream f = null;
		int keyTerms = 0;
		try {
			f = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ObjectInput s = null;
		
		
		try {
				s = new ObjectInputStream(f);
				if (!isForward) {
						invIndex  = (Map<String, LinkedList<Integer[]>>)s.readObject();
						keyTerms += invIndex.size();
		 	    }
				 else {
						 fwdIndex  = (Map<Integer, LinkedList<Integer>>)s.readObject();
						 keyTerms += fwdIndex.size();
				 }
				s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return keyTerms;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName; boolean isForward;
		CheckingIndex ci = new CheckingIndex();	
		//termDictionary = new HashMap<Integer,String>();
		Properties properties = null;
		try {
			properties = FileUtil.loadProperties("/Users/aswinbharadwaj/Documents/IR/project1/WikiIndexer/files/properties.config");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IndexReader termrdr = new IndexReader(properties, INDEXFIELD.TERM);
		IndexReader authrdr = new IndexReader(properties, INDEXFIELD.AUTHOR);
		IndexReader catrdr = new IndexReader(properties, INDEXFIELD.CATEGORY);
		IndexReader linkrdr = new IndexReader(properties, INDEXFIELD.LINK);
		
		System.out.println("Terms:"+termrdr.getTotalKeyTerms()+" "+"Authors:"+authrdr.getTotalKeyTerms()+"Categories:"+catrdr.getTotalKeyTerms()+"Links:"+linkrdr.getTotalValueTerms());
		
		System.out.println(termrdr.getTopK(100)+" "+termrdr.getPostings("Internet"));
		System.out.println(authrdr.getTopK(10)+" "+authrdr.getPostings("ChrisGualtieri"));
		System.out.println(catrdr.getTopK(10)+" "+catrdr.getPostings("Jupiter Trojans (Greek camp)"));
		System.out.println(linkrdr.getTopK(10));
		System.out.println(catrdr.query(new String[]{"Jupiter Trojans (Trojan camp)","Asteroids named from Greek mythology"}));
		
	}

}
