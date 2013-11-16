/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer.test;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;
import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.indexer.IndexerException;
import edu.buffalo.cse.ir.wikiindexer.indexer.LocalDictionary;
import edu.buffalo.cse.ir.wikiindexer.test.PropertiesBasedTest;

/**
 * @author nikhillo
 * 
 */
@RunWith(Parameterized.class)
public class LocalDictionaryTest extends PropertiesBasedTest {

	public LocalDictionaryTest(Properties props) {
		super(props);
	}

	/**
	 * Test method for
	 * {@link edu.buffalo.cse.ir.wikiindexer.indexer.LocalDictionary#lookup(java.lang.String)}
	 * .
	 */
	@Test
	public final void testLookup() {
		// test that it creates entries
		INDEXFIELD testarr[] = INDEXFIELD.values();

		for (INDEXFIELD fld : testarr) {
			LocalDictionary dict = new LocalDictionary(idxProps, fld);
			int retVal = dict.lookup("test");
			assertTrue(retVal > 0);

			// test same return value
			assertEquals(retVal, dict.lookup("test"));

			// test different values
			assertNotEquals(retVal, dict.lookup("tests"));

			// test case sensitivity
			assertNotEquals(retVal, dict.lookup("Test"));
			assertNotEquals(retVal, dict.lookup("tEst"));
			assertNotEquals(retVal, dict.lookup("TEST"));
			
			dict = null;
		}
	}

	/**
	 * Test method for
	 * {@link edu.buffalo.cse.ir.wikiindexer.indexer.Dictionary#exists(java.lang.String)}
	 * .
	 */
	@Test
	public final void testExists() {
		INDEXFIELD testarr[] = INDEXFIELD.values();

		for (INDEXFIELD fld : testarr) {
			LocalDictionary dict = new LocalDictionary(idxProps, fld);
			
			//test with empty
			assertFalse(dict.exists("test"));
			
			//assert after addition
			dict.lookup("test");
			assertTrue(dict.exists("test"));

			// test with different values
			assertFalse(dict.exists("tests"));

			// test case sensitivity
			assertFalse(dict.exists("Test"));
			assertFalse(dict.exists("tEst"));
			assertFalse(dict.exists("TEST"));
			
			dict = null;
		}
	}
	
	@Test
	public final void testWritetoDisk() {
		INDEXFIELD testarr[] = INDEXFIELD.values();

		for (INDEXFIELD fld : testarr) {
			LocalDictionary dict = new LocalDictionary(idxProps, fld);
			for (int i = 0; i < 1000; i++) {
				dict.lookup("test"+i);
			}
			try {
				dict.writeToDisk();
				Map<String,Integer> actual = dict.getDictionary();
				FileInputStream f = null;
				Map<String, Integer> read;
				try {
					f = new FileInputStream(FileUtil.getRootFilesFolder(idxProps)+fld.name().toLowerCase());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    ObjectInput s = null;
				try {
					s = new ObjectInputStream(f);
					try {
						read  = (Map<String, Integer>)s.readObject();
						assertEquals(actual.size(), read.size());
						for (int i = 0; i < 1000; i++) {
							assertEquals(actual.get("test"+i), read.get("test"+i));
						}
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IndexerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	

	/**
	 * Test method for
	 * {@link edu.buffalo.cse.ir.wikiindexer.indexer.Dictionary#query(java.lang.String)}
	 * .
	 */
	
	@Test
	public final void testQuery() {
		INDEXFIELD testarr[] = INDEXFIELD.values();

		for (INDEXFIELD fld : testarr) {
			LocalDictionary dict = new LocalDictionary(idxProps, fld);
			
			//pre-populate with known terms
			String[] myterms = {"test","best","crest","zest","testy","tether","temper", "teat","tempest"};
			
			for (String term : myterms) {
				dict.lookup(term);
			}
			
			//exact match
			assertArrayEquals(new Object[] {"test"}, getSortedArray(dict.query("test")));
			
			//no match for a given exact match
			assertNull(dict.query("doom"));
			
			//wildcard at start
			assertArrayEquals(new Object[] {"best","crest","tempest","test","zest"}, getSortedArray(dict.query("*est")));
			
			//wildcard at end
			assertArrayEquals(new Object[] {"teat","temper","tempest","test","testy","tether"}, getSortedArray(dict.query("te*")));
			
			//wildcard in the middle
			assertArrayEquals(new Object[] {"teat","tempest","test"}, getSortedArray(dict.query("te*t")));
			
			dict = null;
		}
	}
	
	private Object[] getSortedArray(Collection<String> input) {
		List<String> templist = new ArrayList<String>(input);
		Collections.sort(templist);
		return templist.toArray();
	}
	
	/**
	 * Test method for
	 * {@link edu.buffalo.cse.ir.wikiindexer.indexer.Dictionary#getTotalTerms()}
	 * .
	 */
	@Test
	public final void testGetTotalTerms() {
		INDEXFIELD testarr[] = INDEXFIELD.values();

		for (INDEXFIELD fld : testarr) {
			LocalDictionary dict = new LocalDictionary(idxProps, fld);
			
			//empty dictionary
			assertEquals(0, dict.getTotalTerms());
			
			
			//large loop addition
			for (int i = 0; i < 1000; i++) {
				dict.lookup("test"+i);
				assertEquals(i+1, dict.getTotalTerms());
			}
			
			//size with no new elements added
			dict.lookup("test500");
			assertEquals(1000, dict.getTotalTerms());
			
			dict = null;
		}
	}

}
