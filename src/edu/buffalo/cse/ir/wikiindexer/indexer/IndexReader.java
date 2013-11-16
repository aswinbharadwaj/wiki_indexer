/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;

/**
 * @author nikhillo
 * This class is used to introspect a given index
 * The expectation is the class should be able to read the index
 * and all associated dictionaries.
 */
public class IndexReader {
	/**
	 * Constructor to create an instance 
	 * @param props: The properties file
	 * @param field: The index field whose index is to be read
	 */
	
	private String type;
	private String fileName;
	private boolean isForward;
	private String docDictFile;
	private Map<String,LinkedList<Integer[]>> invIndex;
	private Map<Integer,LinkedList<Integer>> fwdIndex;
	
	public IndexReader(Properties props, INDEXFIELD field) {
		//TODO: Implement this method
		docDictFile = FileUtil.getRootFilesFolder(props)+"link";
		if ( field.name().equals("TERM") ){
			//termDictionary = new HashMap<Integer,String>();
			isForward = false;
			type = "term";
			fileName = FileUtil.getRootFilesFolder(props)+"termindex";
		}
		else if ( field.name().equals("AUTHOR") ){
			//authorDictionary = new HashMap<Integer,String>();
			isForward = false;
			type = "author";
			fileName = FileUtil.getRootFilesFolder(props)+"authorindex";
		}
		else if ( field.name().equals("CATEGORY") ){
			//categoryDictionary = new HashMap<Integer,String>();
			isForward = false;
			type = "category";
			fileName = FileUtil.getRootFilesFolder(props)+"categoryindex";
		}
		else {
			//linkDictionary = new HashMap<Integer,String>();
			isForward = true;
			type = "link";
			fileName = FileUtil.getRootFilesFolder(props)+"linkindex";
		}
		if(isForward)
			fwdIndex = new HashMap<Integer,LinkedList<Integer>>();
		else
			invIndex = new HashMap<String,LinkedList<Integer[]>>();
	}
	
	
	public void populateIndexMap() {
		FileInputStream f = null;
		try {
			f = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ObjectInput s = null;
		try {
			s = new ObjectInputStream(f);
			try {
				 if (!isForward) {
						 invIndex  = (Map<String, LinkedList<Integer[]>>)s.readObject();
				 }	 
				 else {
						 fwdIndex  = (Map<Integer, LinkedList<Integer>>)s.readObject();
					 
				 }
				 
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Map<String,Integer> getDocDictAvailable() {
		FileInputStream f = null;
		HashMap<String, Integer> dict = null;
		try {
			f = new FileInputStream(docDictFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ObjectInput s = null;
			try {
				s = new ObjectInputStream(f);
				try {
					dict = (HashMap<String, Integer>)s.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return dict;
	}
	
	/**
	 * Method to get the total number of terms in the key dictionary
	 * @return The total number of terms as above
	 */
	public int getTotalKeyTerms() {
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
	
	/**
	 * Method to get the total number of terms in the value dictionary
	 * @return The total number of terms as above
	 */
	public int getTotalValueTerms() {
		//TODO: Implement this method
		Map<String, Integer> dict;
		if ( (dict = getDocDictAvailable()) != null ) {
			return dict.size();
		}
		populateIndexMap();
		Set<Integer> postingset = new HashSet<Integer>();
		if (!isForward) {
			for (LinkedList<Integer[]> postings : invIndex.values())
				for (Integer[] posting: postings)
					postingset.add(posting[0]);
		}
			
		else {
			for (LinkedList<Integer> postings : fwdIndex.values()) {
						postingset.addAll(postings);
			}
		}
		return postingset.size();
	}
	
	/**
	 * Method to retrieve the postings list for a given dictionary term
	 * @param key: The dictionary term to be queried
	 * @return The postings list with the value term as the key and the
	 * number of occurrences as value. An ordering is not expected on the map
	 */
	public Map<String, Integer> getPostings(String key) {
		//TODO: Implement this method
		populateIndexMap();
		Map<String, Integer> postings = null;
		LinkedList<Integer[]> invPostings;
		Map<String, Integer> dictionary;
		dictionary = getDocDictAvailable();
		if ( !isForward ) {
			invPostings = invIndex.get(key);
			if (invPostings != null) {
				postings = new HashMap<String, Integer>();
				for (Integer []posting: invPostings) {
					for (Entry<String, Integer> etr: dictionary.entrySet())
						if ( etr.getValue().equals(posting[0]) ){
							postings.put(etr.getKey(),posting[1]);
							break;
						}
							
				}
		    }
			
	    }
		else {
			Map<String, Integer> dict;
			if ( (dict = getDocDictAvailable()) != null ) {
				LinkedList<Integer> linklist = fwdIndex.get(dict.get(key));
				postings = new HashMap<String, Integer>();
				for(Integer link: linklist)
					for (Entry<String, Integer> etr: dictionary.entrySet())
						if ( etr.getValue().equals(link) ){
							postings.put(etr.getKey(),1);
							break;
						}
			}
		}
			
	   return postings;
	}
	
	/**
	 * Method to get the top k key terms from the given index
	 * The top here refers to the largest size of postings.
	 * @param k: The number of postings list requested
	 * @return An ordered collection of dictionary terms that satisfy the requirement
	 * If k is more than the total size of the index, return the full index and don't 
	 * pad the collection. Return null in case of an error or invalid inputs
	 */
	public Collection<String> getTopK(int k) {
		//TODO: Implement this method
		LinkedList<String> listtopK;
		Map<String, Integer> dictionary;
		dictionary = getDocDictAvailable();
		TreeMap<Integer, LinkedList<String>> topK = new TreeMap<Integer, LinkedList<String>>(new Comparator<Integer>(){
			public int compare(Integer a, Integer b){
				if ( a > b)
					return -1;
				else if ( a==b )
					return 0;
				else
					return 1;
			}});
		populateIndexMap();
		LinkedList<String> terms;
		int key;
		if ( !isForward ) {
			for (Entry<String,LinkedList<Integer[]>> etr : invIndex.entrySet()) {
				key = etr.getValue().size();
				if (topK.get(key) != null) {
					terms = topK.get(key);
					terms.add(etr.getKey());
					topK.put(key, terms);
				}
				else {
					terms = new LinkedList<String>();
					terms.add(etr.getKey());
					topK.put(key, terms);
				}
			}
		}
		else {
			for (Entry<Integer,LinkedList<Integer>> etr : fwdIndex.entrySet()) {
				key = etr.getValue().size();
				if (topK.get(key) != null) {
					terms = topK.get(key);
					for (Entry<String, Integer> inetr: dictionary.entrySet())
						if ( inetr.getValue().equals(etr.getKey()) ){
							terms.add(inetr.getKey());
							break;
						}
					topK.put(key, terms);
				}
				else {
					terms = new LinkedList<String>();
					for (Entry<String, Integer> inetr: dictionary.entrySet())
						if ( inetr.getValue().equals(etr.getKey()) ){
							terms.add(inetr.getKey());
							break;
						}
					topK.put(key, terms);
				}
			}
		}
		listtopK = new LinkedList<String>();
		int i = 0;
		boolean done = false;
		for ( Entry<Integer, LinkedList<String>> val: topK.entrySet() ) {
			for ( String keyVal : val.getValue() ) {
				if (i < k){
					listtopK.add(keyVal);
					i++;
				}
				else {
					done = true;
					break;
				}
					
			}
			if (done)
				break;
		}
		return listtopK;
	}
	
	/**
	 * Method to execute a boolean AND query on the index
	 * @param terms The terms to be queried on
	 * @return An ordered map containing the results of the query
	 * The key is the value field of the dictionary and the value
	 * is the sum of occurrences across the different postings.
	 * The value with the highest cumulative count should be the
	 * first entry in the map.
	 */
	public Map<String, Integer> query(String... terms) {
		//TODO: Implement this method (FOR A BONUS)
		Map<String, Integer> posting;
		Map<String, Integer> boolMap = new HashMap<String, Integer>();
		for (String term: terms) {
			posting = getPostings(term);
			for (Entry<String, Integer> etr: posting.entrySet()){
				if (boolMap.containsKey(etr.getKey()))
					boolMap.put(etr.getKey(), etr.getValue()+boolMap.get(etr.getKey()));
				else 
					boolMap.put(etr.getKey(), etr.getValue());
			}
		}
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>( boolMap.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<String, Integer>>() {
		            public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 ) {
		            	o1.getValue().compareTo(o2.getValue());
		                if ( o1.getValue() > o2.getValue() )
		                		return -1;
		                else if ( o1.getValue() == o2.getValue() )
		                		return 0;
		                else
		                		return 1;
		            }
		} );

		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : list) {
		   result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}
}
