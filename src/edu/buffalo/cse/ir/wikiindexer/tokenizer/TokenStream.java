/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class represents a stream of tokens as the name suggests.
 * It wraps the token stream and provides utility methods to manipulate it
 * @author nikhillo
 *
 */
public class TokenStream implements Iterator<String>{
	
	//Design decision to make -> list/set
	private List<String> tokenStream ;
	//Consider replacing with a listiterator
	private int position;
	
	
	public TokenStream() {
		position = 0;
	}
	
	/**
	 * Default constructor
	 * @param bldr: THe stringbuilder to seed the stream
	 */
	
	public TokenStream(StringBuilder bldr) {
		//TODO: Implement this method
		this();
		if ( bldr == null || bldr.toString().equals("")) {
			tokenStream = null;
			return;
		}
		tokenStream = new LinkedList<String>();
		append(bldr.toString());
	}
	
	/**
	 * Overloaded constructor
	 * @param bldr: THe stringbuilder to seed the stream
	 */
	
	public TokenStream(String string) {
		//TODO: Implement this method
		this();
		if ( string == null || string.equals("")) {
			tokenStream = null;
			return;
		}
		tokenStream = new LinkedList<String>();
		append(string);
	}
	
	/**
	 * Method to append tokens to the stream
	 * @param tokens: The tokens to be appended
	 */
	public void append(String... tokens) {
		//TODO: Implement this method
		if ( tokenStream == null )
			return;
		if ( tokens != null )
			for (String token: tokens) {
				if ( token != null && !token.equals(""))
					tokenStream.add(token);
			}
			
	}
	
	/**
	 * Method to retrieve a map of token to count mapping
	 * This map should contain the unique set of tokens as keys
	 * The values should be the number of occurrences of the token in the given stream
	 * @return The map as described above, no restrictions on ordering applicable
	 */
	public Map<String, Integer> getTokenMap() {
		//TODO: Implement this method
		/*if (tokenStream.iterator().next() == null || tokenStream.iterator().next() == "")
			return null;
		*/
		if ( tokenStream == null )
			return null;
		Iterator<String> iterator = tokenStream.iterator();
		Map<String, Integer> tokenMap = new HashMap<String, Integer>();
		String token;
		while ( iterator.hasNext() ) {
			token = iterator.next();
			if ( tokenMap.containsKey(token)) {
				tokenMap.put(token, tokenMap.get(token)+1);
			}
			else
				tokenMap.put(token, 1);
		}
		return tokenMap;
	}
	
	/**
	 * Method to get the underlying token stream as a collection of tokens
	 * @return A collection containing the ordered tokens as wrapped by this stream
	 * Each token must be a separate element within the collection.
	 * Operations on the returned collection should NOT affect the token stream
	 */
	public Collection<String> getAllTokens() {
		//TODO: Implement this method
		if ( tokenStream == null )
			return null;
		return new LinkedList<String>(tokenStream);
	}
	
	/**
	 * Method to query for the given token within the stream
	 * @param token: The token to be queried
	 * @return: THe number of times it occurs within the stream, 0 if not found
	 */
	public int query(String token) {
		//TODO: Implement this method
		if ( tokenStream == null )
			return 0;
		if ( getTokenMap() == null )
			return 0;
		if ( getTokenMap().get(token) != null )
			return getTokenMap().get(token);
		else
			return 0;
	}
	
	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasNext() {
		// TODO: Implement this method
		if ( tokenStream == null)
			return false;
		if ( position < tokenStream.size() )
			return true;
		else
			return false;
	}
	
	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasPrevious() {
		//TODO: Implement this method
		if ( tokenStream == null )
			return false;
		if ( position > 0 )
			return true;
		else
			return false;
	}
	
	/**
	 * Iterator method: Method to get the next token from the stream
	 * Callers must call the set method to modify the token, changing the value
	 * of the token returned by this method must not alter the stream
	 * @return The next token from the stream, null if at the end
	 */
	public String next() {
		// TODO: Implement this method
		if ( tokenStream == null )
			return null;
		String nextToken;
		if ( hasNext() ) {
			nextToken = tokenStream.get(position++);
			return nextToken;
		}
		else
			return null;
	}
	
	/**
	 * Iterator method: Method to get the previous token from the stream
	 * Callers must call the set method to modify the token, changing the value
	 * of the token returned by this method must not alter the stream
	 * @return the previous token from stream, null if at the start
	 */
	public String previous() {
		//TODO: Implement this method
		if ( tokenStream == null )
			return null;
		String prevToken;
		if ( hasPrevious() ) {
			prevToken = tokenStream.get(--position);
			return prevToken;
		} 
		else
			return null;
	}
	
	/**
	 * Iterator method: Method to remove the current token from the stream
	 */
	public void remove() {
		// TODO: Implement this method
		if ( tokenStream == null )
			return;
		if ( position < tokenStream.size() ) {
			tokenStream.remove(position);
		}
			
	}
	
	/**
	 * Method to merge the current token with the previous token, assumes whitespace
	 * separator between tokens when merged. The token iterator should now point
	 * to the newly merged token (i.e. the previous one)
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithPrevious() {
		//TODO: Implement this method
		if ( tokenStream == null )
			return false;
		if ( !hasPrevious()  || position >= tokenStream.size())
			return false;
		else {
			
			tokenStream.set( position-1, tokenStream.get(position-1) + ' ' + tokenStream.get(position));
			tokenStream.remove(position--);
			return true;
		}
	}
	
	/**
	 * Method to merge the current token with the next token, assumes whitespace
	 * separator between tokens when merged. The token iterator should now point
	 * to the newly merged token (i.e. the current one)
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithNext() {
		//TODO: Implement this method
		if ( tokenStream == null )
			return false;
		if ( !hasNext() || position == tokenStream.size()-1 )
			return false;
		else {
			tokenStream.set( position, tokenStream.get(position) + ' ' + tokenStream.get(position+1) );
			tokenStream.remove(position+1);
			return true;
		}
	}
	
	/**
	 * Method to replace the current token with the given tokens
	 * The stream should be manipulated accordingly based upon the number of tokens set
	 * It is expected that remove will be called to delete a token instead of passing
	 * null or an empty string here.
	 * The iterator should point to the last set token, i.e, last token in the passed array.
	 * @param newValue: The array of new values with every new token as a separate element within the array
	 */
	public void set(String... newValue) {
		//TODO: Implement this method
		if ( tokenStream == null )
			return;
		int count = 0;
		if ( position >=  tokenStream.size() )
			return;
		else {
			 int start = position;
			 for(String val: newValue) {
				if (val != null && !val.equals("")) {
						if ( position != start )
							tokenStream.add(position++, val);
						else
							tokenStream.set(position++, val);
				}
				else
					count++;
			}
			if ( count != 1 )
				position--;
		}
	}
	
	/**
	 * Iterator method: Method to reset the iterator to the start of the stream
	 * next must be called to get a token
	 */
	public void reset() {
		//TODO: Implement this method
		position = 0;
	}
	
	/**
	 * Iterator method: Method to set the iterator to beyond the last token in the stream
	 * previous must be called to get a token
	 */
	public void seekEnd() {
		if ( tokenStream == null )
			return;
		position = tokenStream.size();
	}
	
	/**
	 * Method to merge this stream with another stream
	 * @param other: The stream to be merged
	 */
	public void merge(TokenStream other) {
		//TODO: Implement this method
		if ( tokenStream == null )
			if ( other != null ) {
				tokenStream = new LinkedList<String>();
				tokenStream.addAll(other.getAllTokens());
				return;
			}
			else
				return;
		if ( other != null && other.next() != null)
			tokenStream.addAll(other.getAllTokens());
	}
}
