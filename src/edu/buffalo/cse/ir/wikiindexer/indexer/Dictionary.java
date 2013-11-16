/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;

/**
 * @author nikhillo
 * An abstract class that represents a dictionary object for a given index
 */
public abstract class Dictionary implements Writeable {
	
	private Map<String, Integer> dictionary;
	private String fileName;
	private String type;
	private static int uniquetermCount = 0;
	
	public Dictionary (Properties props, INDEXFIELD field) {
		dictionary = new HashMap<String, Integer>();
		if ( field.name().equals("TERM") ){
			type = "term";
			fileName = FileUtil.getRootFilesFolder(props)+"term";
		}
		else if ( field.name().equals("AUTHOR") ){
			type = "author";
			fileName = FileUtil.getRootFilesFolder(props)+"author";
		}
		else if ( field.name().equals("CATEGORY") ){
			type = "category";
			fileName = FileUtil.getRootFilesFolder(props)+"category";
		}
		else {
			type = "link";
			fileName = FileUtil.getRootFilesFolder(props)+"link";
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {
		// TODO Implement this method
		FileOutputStream f = null;
		try {
			f = new FileOutputStream(fileName); // consider a replace
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    ObjectOutput s = null;
		try {
			s = new ObjectOutputStream(f);
			s.writeObject(dictionary);
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		// TODO Implement this method

	}
	
	/**
	 * Method to check if the given value exists in the dictionary or not
	 * Unlike the subclassed lookup methods, it only checks if the value exists
	 * and does not change the underlying data structure
	 * @param value: The value to be looked up
	 * @return true if found, false otherwise
	 */
	public boolean exists(String value) {
		//TODO Implement this method
		if (dictionary.containsKey(value))
			return true;
		else
			return false;
	}
	
	/**
	 * MEthod to lookup a given string from the dictionary.
	 * The query string can be an exact match or have wild cards (* and ?)
	 * Must be implemented ONLY AS A BONUS
	 * @param queryStr: The query string to be searched
	 * @return A collection of ordered strings enumerating all matches if found
	 * null if no match is found
	 */
	public Collection<String> query(String queryStr) {
		//TODO: Implement this method (FOR A BONUS)
		List<String> result = new LinkedList<String>();
		StringBuffer matcher;
		if (queryStr.contains("*"))
			queryStr = queryStr.replaceAll("\\*",".*");
		if (queryStr.contains("?"))
			queryStr = queryStr.replaceAll("\\?",".");
			for (Entry<String,Integer> etr: dictionary.entrySet()) {
				if( etr.getKey().matches(queryStr) )
					result.add(etr.getKey());
			}
		if (result.isEmpty())
			return null;
		else
			return result;
	}
	
	/**
	 * Method to get the total number of terms in the dictionary
	 * @return The size of the dictionary
	 */
	public int getTotalTerms() {
		return dictionary.size();
	}
	
	/*
	 * Method to compute the MD5 hash of the terms, thus ensuring very unique term ids
	 * @param input the input term as String
	 * @return the hashcode of the hash byte array
	 */
	
	public int getHash(String input) {
		MessageDigest digest;
		byte[] hash = {};
		try {
			 digest = java.security.MessageDigest.getInstance("MD5");
			 digest.update(input.getBytes());
			 hash = digest.digest();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  hash.hashCode();	
	}
	
	public Map<String, Integer> getDictionary(){
		return dictionary;
	}
	
	public String getType(){
		return type;
	}
	
	public static void incrementUniqueCount() {
		++uniquetermCount;
	}
}
