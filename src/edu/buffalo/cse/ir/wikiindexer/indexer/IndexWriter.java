/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import edu.buffalo.cse.ir.wikiindexer.AppendingObjectOutputStream;
import edu.buffalo.cse.ir.wikiindexer.FileUtil;

/*
 * We need to develop two types of indexes -> forward and inverted
 * The forward index does not store the number of occurrences of the 
 * key field(term,author,category,link) with the value in the postings list
 */

/**
 * @author nikhillo
 * This class is used to write an index to the disk
 * 
 */
public class IndexWriter implements Writeable {
	
	
	// Postings List
	private Map<String,LinkedList<Integer[]>> invIndex;
	private Map<Integer,LinkedList<Integer>> fwdIndex;
	private int partitionNumber;
	private int POSTINGSSIZE;
	private final int MB = 1024*1024;
	private String type;
	private String fileName;
	private boolean isForward;
	private int writecount;
	private Runtime runtime;
	
	/**
	 * Constructor that assumes the underlying index is inverted
	 * Every index (inverted or forward), has a key field and the value field
	 * The key field is the field on which the postings are aggregated
	 * The value field is the field whose postings we are accumulating
	 * For term index for example:
	 * 	Key: Term (or term id) - referenced by TERM INDEXFIELD
	 * 	Value: Document (or document id) - referenced by LINK INDEXFIELD
	 * @param props: The Properties file
	 * @param keyField: The index field that is the key for this index
	 * @param valueField: The index field that is the value for this index
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField, INDEXFIELD valueField) {
		this(props, keyField, valueField, false);
	}
	
	/**
	 * Overloaded constructor that allows specifying the index type as
	 * inverted or forward
	 * Every index (inverted or forward), has a key field and the value field
	 * The key field is the field on which the postings are aggregated
	 * The value field is the field whose postings we are accumulating
	 * For term index for example:
	 * 	Key: Term (or term id) - referenced by TERM INDEXFIELD
	 * 	Value: Document (or document id) - referenced by LINK INDEXFIELD
	 * @param props: The Properties file
	 * @param keyField: The index field that is the key for this index
	 * @param valueField: The index field that is the value for this index
	 * @param isForward: true if the index is a forward index, false if inverted
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField, INDEXFIELD valueField, boolean isForward) {
		//TODO: Implement this method
		
		this.isForward = isForward;
		writecount = 0;
		if(isForward)
			fwdIndex = new HashMap<Integer,LinkedList<Integer>>();
		else
			invIndex = new HashMap<String,LinkedList<Integer[]>>();
		partitionNumber = 0;
		POSTINGSSIZE = 100;
		runtime = Runtime.getRuntime();
		if ( keyField.name().equals("TERM") ){
			//termDictionary = new HashMap<Integer,String>();
			type = "term";
			fileName = FileUtil.getRootFilesFolder(props)+"termindex";
		}
		else if ( keyField.name().equals("AUTHOR") ){
			//authorDictionary = new HashMap<Integer,String>();
			type = "author";
			fileName = FileUtil.getRootFilesFolder(props)+"authorindex";
		}
		else if ( keyField.name().equals("CATEGORY") ){
			//categoryDictionary = new HashMap<Integer,String>();
			type = "category";
			fileName = FileUtil.getRootFilesFolder(props)+"categoryindex";
		}
		else {
			//linkDictionary = new HashMap<Integer,String>();
			type = "link";
			fileName = FileUtil.getRootFilesFolder(props)+"linkindex";
		}
	}
	
	/**
	 * Method to make the writer self aware of the current partition it is handling
	 * Applicable only for distributed indexes.
	 * @param pnum: The partition number
	 */
	public void setPartitionNumber(int pnum) {
		//TODO: Optionally implement this method
		partitionNumber = pnum;
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param keyId: The id for the key field, pre-converted
	 * @param valueId: The id for the value field, pre-converted
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, int valueId, int numOccurances) throws IndexerException { // this is for the link index ( forward index )
		//TODO: Implement this method
		//Check if the memory is full
		//Check for the filled postings list
		//If full then double the size of the list
		
		LinkedList<Integer> links = null;
		if ( (runtime.freeMemory() / MB) > 10 ) {
			if ( !fwdIndex.containsKey(keyId) ) {
				links = new LinkedList<Integer>();
				//fwdIndex.put(keyId, links);
			}
			else {
				links = fwdIndex.get(keyId);
			}
			/*
			if (links.size() >= POSTINGSSIZE) {
				POSTINGSSIZE = 2*POSTINGSSIZE;
				Collections.sort(links);
			}
			*/
			links.add(valueId);
			fwdIndex.put(keyId, links);
		}
		else {
			for (Entry<Integer,LinkedList<Integer>> etr : fwdIndex.entrySet()) {
				LinkedList<Integer> list = etr.getValue();
				Collections.sort(list);
				etr.setValue(list);
			}
			writeToDisk();
		}
			
		
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param keyId: The id for the key field, pre-converted
	 * @param value: The value for the value field
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, String value, int numOccurances) throws IndexerException { 
		//TODO: Implement this method
		if (isForward)
			addToIndex(keyId, Integer.parseInt(value), numOccurances);
		else
			addToIndex(String.valueOf(keyId), Integer.parseInt(value), numOccurances);
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param key: The key for the key field
	 * @param valueId: The id for the value field, pre-converted
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(String key, int valueId, int numOccurances) throws IndexerException { // this is for the term index, author index and category index
		//TODO: Implement this method
		
		
		LinkedList<Integer[]> links = null;
		if ( (runtime.freeMemory() / MB) > 10 ) {
			if ( !invIndex.containsKey(key) ) {
				links = new LinkedList<Integer[]>();
			}
			else {
				links = invIndex.get(key);
			}
			/*
			if (links.size() >= POSTINGSSIZE) {
				POSTINGSSIZE = 2*POSTINGSSIZE;
				Collections.sort((List<Integer[]>)links, new Comparator<Integer[]>(){
					public int compare(Integer[] a, Integer[] b){
						return a[0].compareTo(b[0]);
				}});
			}
			*/
			links.add(new Integer[]{valueId,numOccurances});
			invIndex.put(key, links);
		}
		else {
			for (Entry<String,LinkedList<Integer[]>> etr : invIndex.entrySet()) {
				LinkedList<Integer[]> list = etr.getValue();
				Collections.sort((List<Integer[]>)list, new Comparator<Integer[]>(){
					public int compare(Integer[] a, Integer[] b){
						return a[0].compareTo(b[0]);
				}});
				etr.setValue(list);
			}
			writeToDisk();
		}
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param key: The key for the key field
	 * @param value: The value for the value field
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(String key, String value, int numOccurances) throws IndexerException {
		//TODO: Implement this method
		if (isForward)
			addToIndex(Integer.parseInt(key), Integer.parseInt(value), numOccurances);
		else
			addToIndex(key, Integer.parseInt(value), numOccurances);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {
		// TODO Implement this method
		FileOutputStream f = null;
		try {
			if ( writecount > 0)
				f = new FileOutputStream(fileName, true); // consider an append
			else
				f = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    ObjectOutput s = null;
		try {
			if ( writecount > 0 )
				s = new AppendingObjectOutputStream(f);
			else
				s = new ObjectOutputStream(f);
			if (isForward)
			{
				s.writeObject(fwdIndex);
				fwdIndex.clear();
			}
			else {
				s.writeObject(invIndex);
				invIndex.clear();
			}
			s.close();
			writecount++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void populateIndexMap() {
		FileInputStream f = null;
		Map<String,LinkedList<Integer[]>> tempinvIndex;
		Map<Integer,LinkedList<Integer>> tempfwdIndex;
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
					 invIndex.clear();
					 for ( int i = 0; i < writecount; ++i ) {
						 tempinvIndex  = (Map<String, LinkedList<Integer[]>>)s.readObject();
						 for (Entry<String,LinkedList<Integer[]>> etr : tempinvIndex.entrySet()) 
							 if(invIndex.containsKey(etr.getKey())) {
								 LinkedList<Integer[]> postings = invIndex.get(etr.getKey());
								 postings.addAll(etr.getValue());
								 etr.setValue(postings);
							 }
							 else
								 invIndex.put(etr.getKey(), etr.getValue());
					 }
				 }	 
				 else {
					 fwdIndex.clear();
					 for ( int i = 0; i < writecount; ++i  ) {
						 tempfwdIndex  = (Map<Integer, LinkedList<Integer>>)s.readObject();
						 for (Entry<Integer,LinkedList<Integer>> etr : tempfwdIndex.entrySet()) 
							 if(fwdIndex.containsKey(etr.getKey())){
								 LinkedList<Integer> postings = fwdIndex.get(etr.getKey());
								 postings.addAll(etr.getValue());
								 etr.setValue(postings);
							 }
								 
							 else
								 fwdIndex.put(etr.getKey(), etr.getValue());
					 }
					 
				 }
				 
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s.close();
		} catch (EOFException e) {
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		// TODO Implement this method
		populateIndexMap();
		FileOutputStream f = null;
		try {
			f = new FileOutputStream(fileName); // consider an replace
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    ObjectOutput s = null;
		try {
			s = new ObjectOutputStream(f);
			if (isForward)
			{
				s.writeObject(fwdIndex);
				fwdIndex.clear();
			}
			else {
				s.writeObject(invIndex);
				invIndex.clear();
			}
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
