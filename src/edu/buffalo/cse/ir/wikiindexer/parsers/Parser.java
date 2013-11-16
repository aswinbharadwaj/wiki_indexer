/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.parsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Properties;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaParser;
//import edu.buffalo.cse.ir.wikiindexer.saxparser.SaxParser;

/**
 * @author nikhillo
 *
 */
public class Parser{
	
	/* */
	private final Properties props;
	
	/**
	 * 
	 * @param idxConfig
	 * @param parser
	 */
	public Parser(Properties idxProps) {
		props = idxProps;
	}
	
	/* TODO: Implement this method */
	/**
	 * 
	 * @param filename the file to be parsed
	 * @param docs to hold the individual wikipedia pages
	 */
	public void parse(String filename, Collection<WikipediaDocument> docs) {
		
		if ( filename == null || filename.equals(""))
			return;
		
		/* Declaration of the reader object */
				XMLReader p;
			
		
				
		/*
		 * Defining the SaxParser Class which raises events and parses the XML document
		 */
				class SaxParser extends DefaultHandler{
			
			    	int idFromXml;
					String timestampFromXml;
					String authorFromXml;
					String ttl;
					String text;
					WikipediaDocument wdoc;
					Collection<WikipediaDocument> realDocs;
					StringBuffer buffer;
					boolean rightidflag;
					
					
				
				/*
				 * Constructor of the SaxParser inner class
				 * @param realDocs the document collection 
				 */
					
				public SaxParser (Collection<WikipediaDocument> realDocs){
					this.realDocs = realDocs;
					buffer = new StringBuffer();
				}
					
					
				/*
				 * (non-Javadoc)
				 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
				 */
				public void startDocument(){
					System.out.print("\nStarted Parsing XML....");
				}
				
				/*
				 * (non-Javadoc)
				 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
				 */
					
				public void endDocument() {
					System.out.print("\nFinished Parsing XML....");
				}
				
				/*
				 * Method which fires on parsing of every element (tag)
				 * (non-Javadoc)
				 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
				 */
				
				public void startElement(String nameSpaceURI, String localName, String qName, Attributes atts) {
					
					/* Resets the StringBuffer object to receive the contents under each tag */
					
					buffer.delete(0, buffer.length());
					
					/* Check the status of the tag processed currently and set the flag or otherwise. 
					 * This ensures that the id value is the doc id and not the revision or parent id.
					 * */
					
					if ( localName.equals("page") )
						rightidflag = true;
					if ( localName.equals("revision") )
						rightidflag = false;
					
				}
				
				/*
				 * Method which stores to the various fields, values from wikipedia document   
				 * (non-Javadoc)
				 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
				 */
				
				public void endElement(String nameSpaceURI, String localName, String qName) {
					
					if ( localName.equals("page") ){
						
						try {
							
							/* Creating a wdoc for every page encountered */
							wdoc = new WikipediaDocument(idFromXml, timestampFromXml, authorFromXml, ttl);
						
							// Call methods of WikipediaParser to parse and add contents to the document
							
							
							/* removing the wikipedia references <abc ...> </abc> */
							
							text = WikipediaParser.parseTagFormatting(text);
							
							/* removing the wikipedia templates {{ ... }} */
							
							text = WikipediaParser.parseTemplates(text);
							
							text = WikipediaParser.parseSpecialTemplates(text);
							
							text = WikipediaParser.parseTextFormatting(text);
							
							WikipediaParser.parseSection(wdoc, text);
						
							
							/* Adding the parsed document to the collection */
							add(wdoc,realDocs);
							
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					else if (localName.equals("text")) {
						
						text = buffer.toString();
						
						
						
						
					}
					else if (localName.equals("id") && rightidflag){
						idFromXml = Integer.valueOf(buffer.toString());
					}
					else if (localName.equals("timestamp")){
						timestampFromXml = buffer.toString();
					}
					else if (localName.equals("ip") || localName.equals("username")){
						authorFromXml = buffer.toString();
					}
					else if (localName.equals("title")){
						ttl = buffer.toString();
					}
				}
				
				
				public void characters(char[] ch, int start, int length) {
					for(int i = start; i<(start+length); i++){
						buffer.append(ch[i]);
					}
				}
			
		}
		/*
		SaxParser realParser = new SaxParser();
		add(realParser.wdoc,docs);
		*/
		
		try {
			
			// Obtaining the XMLReader from the factory object
			p = XMLReaderFactory.createXMLReader();
			p.setContentHandler(new SaxParser(docs));
			
			// parsing the file
			try {
				p.parse(filename);
			} catch (IOException e) {
				System.err.println("invalid filename/ XML BOM error");
			}
			  
			
		} catch (SAXException e) {
			e.printStackTrace();
		} 
		
		
	}
	/**
	 * Method to add the given document to the collection.
	 * PLEASE USE THIS METHOD TO POPULATE THE COLLECTION AS YOU PARSE DOCUMENTS
	 * For better performance, add the document to the collection only after
	 * you have completely populated it, i.e., parsing is complete for that document.
	 * @param doc: The WikipediaDocument to be added
	 * @param documents: The collection of WikipediaDocuments to be added to
	 */
	private synchronized void add(WikipediaDocument doc, Collection<WikipediaDocument> documents) {
		documents.add(doc);
	}
}
