/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.Tokenizer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument.Section;

/**
 * A Callable document transformer that converts the given WikipediaDocument object
 * into an IndexableDocument object using the given Tokenizer
 * @author nikhillo
 *
 */
public class DocumentTransformer implements Callable<IndexableDocument> {
	Map<INDEXFIELD, Tokenizer> tknizerIndexerMap;
	WikipediaDocument document;
	Tokenizer authorTknzr;
	Tokenizer categoryTknzr;
	Tokenizer linkTknzr;
	Tokenizer termTknzr;
	/**
	 * Default constructor, DO NOT change
	 * @param tknizerMap: A map mapping a fully initialized tokenizer to a given field type
	 * @param doc: The WikipediaDocument to be processed
	 */	
	public DocumentTransformer(Map<INDEXFIELD, Tokenizer> tknizerMap, WikipediaDocument doc) {
		//TODO: Implement this method
				termTknzr = tknizerMap.get(INDEXFIELD.TERM);
				authorTknzr = tknizerMap.get(INDEXFIELD.AUTHOR);
				categoryTknzr = tknizerMap.get(INDEXFIELD.CATEGORY);
				linkTknzr = tknizerMap.get(INDEXFIELD.LINK);
				document = doc;
				
	}
	
	/**
	 * Method to trigger the transformation
	 * @throws TokenizerException Inc ase any tokenization error occurs
	 */
	public IndexableDocument call() throws TokenizerException {
		// TODO Implement this method
		IndexableDocument indexDoc = new IndexableDocument();
		
		TokenStream tokensAuthor = new TokenStream(document.getAuthor());
		authorTknzr.tokenize(tokensAuthor);
		indexDoc.addField(INDEXFIELD.AUTHOR, tokensAuthor);
		
		List<String> tempCategories = document.getCategories();
		TokenStream tokensCategory = null;
		if ( !tempCategories.isEmpty() ) {
			int firstindex = 0;
			while ( tempCategories.get(firstindex).equals("") )
				firstindex++;
			tokensCategory = new TokenStream(tempCategories.get(firstindex));
			for (int i = firstindex+1; i < tempCategories.size(); i++ )
				tokensCategory.append(tempCategories.get(i));
		}
		indexDoc.addField(INDEXFIELD.CATEGORY, tokensCategory);
		
		Set<String> tempLinks = document.getLinks();
		TokenStream tokensLinks = null;
		Iterator<String> iter = tempLinks.iterator();
		if ( !tempLinks.isEmpty() ) {
			String starter = null;
			while ( iter.hasNext() && (starter = iter.next()).equals("") );
			tokensLinks = new TokenStream(starter);
			while (iter.hasNext())
				tokensLinks.append(iter.next());
		}
		indexDoc.addField(INDEXFIELD.LINK, tokensLinks);
		
		TokenStream tokensTerms = null;
		
		List<Section> tempSection = document.getSections();
		if ( !tempSection.isEmpty() ) {
			int firstindex = 0;
			while (tempSection.get(firstindex).getTitle().equals("") && tempSection.get(firstindex).getText().equals(""))
				firstindex++;
			if (!tempSection.get(firstindex).getTitle().equals("")) {
				tokensTerms = new TokenStream(tempSection.get(firstindex).getTitle());
				tokensTerms.append(tempSection.get(firstindex).getText());
			}
			else
				tokensTerms = new TokenStream(tempSection.get(firstindex).getText());
			
			for (int i = firstindex+1; i < tempSection.size(); i++ ) {
				tokensTerms.append(tempSection.get(i).getTitle());
				tokensTerms.append(tempSection.get(i).getText());
			}
				
		}
		/*
		TokenStream tokensTitleAndDate = null;
		
		if ( document.getTitle() != null && !document.getTitle().equals("") ) {
			tokensTitleAndDate = new TokenStream(document.getTitle());
			tokensTitleAndDate.append(document.getPublishDate().toString());
		}			
		else if (document.getPublishDate() != null && !document.getPublishDate().toString().equals("")) 
			tokensTitleAndDate = new TokenStream(document.getPublishDate().toString());
		
		tokensTerms.merge(tokensTitleAndDate);
		*/
		if (tokensTerms != null){
			termTknzr.tokenize(tokensTerms);
		}
		indexDoc.addField(INDEXFIELD.TERM, tokensTerms);
		
		indexDoc.docID = document.getTitle(); //Unique Document ID
		
		return indexDoc;
	}
	
}
