/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;


import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author nikhillo
 * This class represents a subclass of a Dictionary class that is
 * local to a single thread. All methods in this class are
 * assumed thread safe for the same reason.
 */
public class LocalDictionary extends Dictionary {
	
	/**
	 * Public default constructor
	 * @param props: The properties file
	 * @param field: The field being indexed by this dictionary
	 */
	public LocalDictionary(Properties props, INDEXFIELD field) {
		super(props, field);
	}
	
	/**
	 * Method to lookup and possibly add a mapping for the given value
	 * in the dictionary. The class should first try and find the given
	 * value within its dictionary. If found, it should return its
	 * id (Or hash value). If not found, it should create an entry and
	 * return the newly created id.
	 * @param value: The value to be looked up
	 * @return The id as explained above.
	 */
	public int lookup(String value) {
		//TODO Implement this method
		// We have to implement the document dictionary that takes up the wikipedia document's id as it's id
		// We also have to implement the term to it's unique hash value
		if ( value == null){
			return -9999;
		}
		Map<String, Integer> dictionary = getDictionary();
		int hash = 0;
		if ( dictionary.containsKey(value) ){
			hash = dictionary.get(value);
		}
		else{
			incrementUniqueCount();
			hash = getHash(value);
			dictionary.put(value, hash);
		}
			
		return hash;
	}
}
