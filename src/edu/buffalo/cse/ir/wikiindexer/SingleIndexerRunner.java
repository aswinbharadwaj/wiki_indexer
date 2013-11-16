/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Properties;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.indexer.IndexWriter;
import edu.buffalo.cse.ir.wikiindexer.indexer.IndexerException;
import edu.buffalo.cse.ir.wikiindexer.indexer.SharedDictionary;

/**
 * @author nikhillo
 * 
 */
public class SingleIndexerRunner {
	private ConcurrentLinkedQueue<Object[]> pvtQueue;
	private IndexWriter idxWriter;
	private SharedDictionary docDict;
	private boolean lookupBoth;
	private RunnerThread thr;
	
	/**
	 * 
	 * @param props
	 * @param keyfield
	 * @param valueField
	 * @param dict
	 */
	protected SingleIndexerRunner(Properties props, INDEXFIELD keyfield,
			INDEXFIELD valueField, SharedDictionary dict, boolean isFwd) {
		idxWriter = new IndexWriter(props, keyfield, valueField, isFwd);
		docDict = dict;
		lookupBoth = (keyfield == INDEXFIELD.LINK);
		pvtQueue = new ConcurrentLinkedQueue<Object[]>();
		thr = new RunnerThread();
		
	}
	
	/**
	 * 
	 * @param docid
	 * @param map
	 * @throws IndexerException
	 */
	protected void processTokenMap(int docid, Map<String, Integer> map) throws IndexerException {
		String key;
		int value;
		Object[] arrObj;
		for (Entry<String, Integer> etr : map.entrySet()) {
			key = etr.getKey(); // gets the author name, category or the link from the stream
			value = etr.getValue(); // gets the total count of the stream's token in the doc referred to by the docid
			
			if (key != null) {
				arrObj = new Object[3];
				// This is like having a forward index for the link index
				if (lookupBoth) { // is it is link index that we are trying to insert (?) into
					arrObj[0] = docid; 
					arrObj[1] = docDict.lookup(key);  // This will add(?) the link (doc id) into the document dictionary and return the id of the same 
				} else {
					arrObj[0] = key; // for the author and category this is like storing the inverted index
					arrObj[1] = docid;
				}
				
				arrObj[2] = value;
				pvtQueue.add(arrObj);
				
				if (!thr.isRunning) {
					thr.isRunning = true;
					new Thread(thr).start();
				}
			}
		}
	}
	
	protected boolean isFinished() {
		return thr.isComplete && thr.isQueueEmpty();
	}
	
	/**
	 * 
	 * @throws IndexerException
	 */
	protected void cleanup() throws IndexerException {
		thr.setComplete();
	}
	
	/**
	 * 
	 * @author nikhillo
	 *
	 */
	private class RunnerThread implements Runnable {
		private boolean isComplete;
		private boolean isRunning;
		
		/**
		 * 
		 */
		private RunnerThread() {
			
		}
		
		/**
		 * 
		 */
		private void setComplete() {
			isComplete = true;
		}
		
		private boolean isQueueEmpty() {
			synchronized (pvtQueue) {
				return pvtQueue.isEmpty();
			}
		}
		
		/**
		 * 
		 */
		public void run() {
			Object[] etr;
			while (true) {
				etr = pvtQueue.poll();

				if (etr == null) {
					if (isComplete) {
						try {
							System.out.println("All the category/link/author index is written, now calling the final write to disk");
							idxWriter.writeToDisk(); // complete doc collection is done with then write to the disk
						} catch (IndexerException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("All the category/link/author index is written, now calling the cleanup to read and merge and re write into the disk");
						idxWriter.cleanUp();
						break; // everything is done
					} else {
						try {
							Thread.sleep(2000); // 2 seconds -- config maybe?
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					// we have an entry
					try {
						if (lookupBoth) {
							idxWriter.addToIndex((Integer) etr[0], (Integer) etr[1], (Integer) etr[2]); // If it is a link then store the forward index <doc id, link's doc id> and the number of times the link appears in the doc
						} else {
							idxWriter.addToIndex((String) etr[0], (Integer) etr[1], (Integer) etr[2]); // If it is an author or category then add the <author/category, doc id> and the number of times the author or category occurs in the doc
						}
					} catch (IndexerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
