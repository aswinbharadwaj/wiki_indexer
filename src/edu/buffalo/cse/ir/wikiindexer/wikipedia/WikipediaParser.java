/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author nikhillo
 * This class implements Wikipedia markup processing.
 * Wikipedia markup details are presented here: http://en.wikipedia.org/wiki/Help:Wiki_markup
 * It is expected that all methods marked "todo" will be implemented by students.
 * All methods are static as the class is not expected to maintain any state.
 */

/*
 * Method to add the objectified values into the wikipedia documet
 * This method takes in
 * @param wdoc wikipedia document object to add to
 * @param sections list of sections in that document
 * @param links set of links to other wikipedia pages
 * @param categories  list of categories under which the document appears
 * @param langlinks the map of language codes to the URLs of various languages in which the same document is available
 */
public class WikipediaParser {

	
	public static void addToWikipediaDocument(WikipediaDocument wdoc, List<WikipediaDocument.Section> sections, Set<String> links, List<String> categories, Map<String,String> langlinks){
	
		
	}
	
	public static StringBuffer linkProcessing(WikipediaDocument wdoc, StringBuffer temptext) {
		int nesting = -1;
		final int NEST = 100; // Amount of nesting the links can have
		int  start[] = new int[NEST], j = 0, end[] = new int[NEST], k = 0, offset = 0, length;
		boolean newnest = false;
		String processLink;
		String textAndLink[];
		
		for ( int l = 0; l < NEST; ++l ) {
			start[l] = end[l] = 0;
		}
		for (int i = 0; i < temptext.length()-1; ++i) {
			//System.out.println(temptext.charAt(i));
			if (temptext.charAt(i) == '[' && temptext.charAt(i+1) == '['){
				if (nesting == -1){
					newnest = true;
				}
				start[j] = i;
				j++;
				i++;
				nesting++;
				continue;
			}
			else if (temptext.charAt(i) == ']' && temptext.charAt(i+1) == ']'){
				if (j == 0)
					continue;
				j--;
				end[k] = i+1;
				nesting--;
				processLink = temptext.substring(start[j], end[k]+1);
				length = processLink.length();
				if ( processLink.contains("Category:") ) {
					if ( processLink.contains("|") ) {
						processLink = processLink.replaceFirst("\\|[^\\]]*", "");
					}
					if ( processLink.matches("^\\[\\[:.*") ) {
						textAndLink = parseLinks(processLink);
						temptext.replace(start[j], end[k]+1, textAndLink[0]);
						offset = (length-textAndLink[0].length());
						i -= offset;
					}
					else if ( processLink.matches("^\\[\\[\\w.*") ) {
						textAndLink = parseLinks(processLink);
						wdoc.addCategory(textAndLink[0]);
						temptext.replace(start[j], end[k]+1, textAndLink[0]);
						offset = (length-textAndLink[0].length());
						i -= offset;
					}
				
				}
				else if ( processLink.matches("^\\[\\[\\w{2}:.*")) {
					textAndLink = parseLinks(processLink);
					wdoc.addLangLink(textAndLink[0].split(":")[0], textAndLink[0].split(":")[1]);
					temptext.delete(start[j], end[k]+1);
					offset = length;
					i -= offset;
				}
				else {
					textAndLink = parseLinks(processLink);
					if ( textAndLink != null ) {
						wdoc.addLink(textAndLink[1]);
						temptext.replace(start[j], end[k]+1, textAndLink[0]);
						offset = (length-textAndLink[0].length());
					}
					else {
						temptext.delete(start[j], end[k]+1);
						offset = length;
					}
						
					i -= offset;
				}/*
				for ( int l = 0; l <= j; ++l ) {
					start[l] -= offset;
				}*/
				k++;
				offset = 0;
			}
			
			if (nesting == -1 && newnest){
				newnest = false;
				j = k = 0;
				for ( int l = 0; l < NEST; ++l ) {
					start[l] = end[l] = 0;
				}
				/*
				end = i-2;
				//System.out.println(start);
				//System.out.println(end);
				processLink = new StringBuffer(temptext.substring(start-offset, end-offset+1));
				offset += end-start+1;
				*/
			}
			
		}
		return temptext;
	}
	
	public static StringBuffer removeHttpLinks(StringBuffer tempText) {
		Pattern pat;Matcher mat;
		pat = Pattern.compile("\\[[\\w][\\w]*://[^\\]]*?\\]"); // \\-\\w\\+&@#/%\\?\\=~_|\\!:,\\.;]*[\\-\\w\\+&@#/%\\=~_\\|
		mat = pat.matcher(tempText);
		String textAndLink[], link;
		int start,end,offset=0,length, findStart = 0;
		while (mat.find(findStart)) {
			start = mat.start();
			end = mat.end();
			link = tempText.substring(start, end+1);
			length = link.length();
			textAndLink = parseLinks(link);
			offset = length - textAndLink[0].length();
			tempText.replace(start, end+1, textAndLink[0]);
			findStart = end - offset + 1;
		}
		return tempText;
	}
	
	/*
	 * Method to parse the text line by line and provide it to the parseList to remove
	 * list markup
	 * @param section which is the current section text
	 * @param a buffer to hold the line contents
	 * @return the parsed section with the list markup removed
	 */
	
	
	public static StringBuffer parseLineandList(StringBuffer section, StringBuffer buffer){
		int offset = 0,reduce = 0;
		String parsedBuffer;
		for (int i = 0; i < section.length(); ++i) {
			char c;
			if ( ( c = section.charAt(i)) != '\n')
				buffer.append(c);
			else {
				if (buffer.toString().matches("^\\*+.*|^\\#+.*|^;.*|^:.*")) {
					//buffer.replace(0, buffer.length(), parseListItem(buffer.toString()));
					//System.out.println(buffer);
					parsedBuffer = parseListItem(buffer.toString());
					reduce = (buffer.length()-parsedBuffer.length());
					section.replace(offset, i, parsedBuffer);
					i -= reduce;
				}
				buffer.delete(0, buffer.length());
				offset = i+1;
			}
			
		}
		return section;
	}
	
	/*
	 * Method to parse the sections and create individual section titles and corresponding text
	 * @param text representing the wiki text with unparsed section and their text
	 */
	
	public static WikipediaDocument parseSection(WikipediaDocument wdoc, String text){
		
		String title;
		StringBuffer buffer = new StringBuffer(), section = new StringBuffer();
		int cursor = 0;
		int start = 0,end =0;
		boolean singledefaultsection = false;
		
		Pattern pat = Pattern.compile("==+[^=]*==+");
		Matcher mat = pat.matcher(text);
		
		/* Adding the first section and its contents */
		
		/* Check for the section with no title  (orphaned section)*/
		if ( mat.find() ) {
			start = mat.start();
			end = mat.end();
			if (start > cursor) {
				section.append(text.substring(cursor, start));
				section = linkProcessing(wdoc, section);
				section = removeHttpLinks(section);
				section = parseLineandList(section, buffer);
				//wdoc = linkProcessing(wdoc, section);
				wdoc.addSection("Default", section.toString() );
				//System.out.println("\nDefault\n"+section.toString());
				//System.out.println(wdoc.getCategories());
				section.delete(0, section.length());
			}
			title = parseSectionTitle(text.substring(start, end));
			cursor = end + 1;
			start = end + 1;
		} /* Check for document without any sections*/
		else {
			title = "Default";
			//wdoc = linkProcessing(wdoc, section);
			section.append(text.substring(cursor, text.length()));
			section = linkProcessing(wdoc, section);
			section = removeHttpLinks(section);
			parseLineandList(section, buffer);
			wdoc.addSection("Default", section.toString() );
			//System.out.println("\nDefault\n"+section.toString());
			//System.out.println(wdoc.getCategories());
			section.delete(0, section.length());
			singledefaultsection = true;
		}
		/* Split the other sections and their corresponding contents, parsing 
		 * the contents and adding them to the document. This procedure takes 
		 * content between two sections ( invariable of the type of section )
		 * and puts the contents alongside the previously encountered section.
		 */
		while ( mat.find(start) ) {
			
			start = mat.start();
			end = mat.end();
			if( text.substring(start,end).contains("\n") || text.charAt(start-1) != '\n'){
				start++;
				continue;
			}
			section.append(text.substring(cursor, start));
			section = linkProcessing(wdoc, section);
			section = removeHttpLinks(section);
			parseLineandList(section, buffer);
			//wdoc = linkProcessing(wdoc, section);
			wdoc.addSection(title, section.toString() );
			//System.out.println("\n"+title+"\n"+section.toString());
			section.delete(0, section.length());
			title = parseSectionTitle(text.substring(start, end));
			title = linkProcessing(wdoc, new StringBuffer(title)).toString();
			cursor = end+1;
			start = end+1;
		}
		/* Adding the last section and it's contents */
		if ( !singledefaultsection ) {
			section.append(text.substring(cursor, text.length()));
			section = linkProcessing(wdoc, section);
			section = removeHttpLinks(section);
			parseLineandList(section, buffer);
			//wdoc = linkProcessing(wdoc, section);
			wdoc.addSection(title, section.toString() );
			//System.out.println("\n"+title+"\n"+section.toString());
			section.delete(0, section.length());
		}
		return wdoc;
	}
	
	/* TODO */
	/**
	 * Method to parse section titles or headings.
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Sections
	 * @param titleStr: The string to be parsed
	 * @return The parsed string with the markup removed
	 */
	
	public static String parseSectionTitle(String titleStr) {
		
		if( titleStr == null || titleStr == "")
			return titleStr;
		Pattern pat = Pattern.compile("=+\\s*|\\s*=+");
		Matcher mat = pat.matcher(titleStr);
		titleStr = mat.replaceAll("");
		return titleStr;
	}
	
	/* TODO */
	/**
	 * Method to parse list items (ordered, unordered and definition lists).
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Lists
	 * @param itemText: The string to be parsed
	 * @return The parsed string with markup removed
	 */
	public static String parseListItem(String itemText) {
		
		if(itemText == null || itemText == "")
			return itemText;
		
		Matcher mat;Pattern pat;
		StringBuffer parseText;
		int offset,colonIndex;
		
		pat = Pattern.compile("^\\*+ *|^#+ *");
		mat = pat.matcher(itemText);
		itemText = mat.replaceAll("");
		mat.reset();
		parseText = new StringBuffer(itemText);
		pat = Pattern.compile("^: *[^\n]*");
		mat = pat.matcher(itemText);
		offset = 0;
		while(mat.find()){
			parseText.delete(mat.start()-offset, mat.start()+2-offset);
			offset += 2;
		}
		mat.reset();
		itemText = parseText.toString();
		pat = Pattern.compile("^;[^\n]*?");
		mat = pat.matcher(itemText);
		while(mat.find()){
			parseText.delete(mat.start(), mat.start()+1);
		}
		mat.reset();
		itemText = parseText.toString();
		pat = Pattern.compile("^; *[^:]* *: *[^\n]*");
		mat = pat.matcher(itemText);
		offset = 0;
		while(mat.find()){
			parseText.delete(mat.start()-offset, mat.start()+2-offset);
			offset += 2;
			colonIndex = itemText.indexOf(':', mat.start()+2);
			parseText.delete(colonIndex-offset,colonIndex+2-offset);
			offset += 2;
		}
		/*pat = Pattern.compile("^;[^\n]*");
		mat = pat.matcher(itemText);
		offset = 0;
		while(mat.find()){
			parseText.delete(mat.start()-offset, mat.start()+1-offset);
			offset += 2;
		}
		*/
		return parseText.toString();
	}
	
	/* TODO */
	/**
	 * Method to parse text formatting: bold and italics.
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Text_formatting first point
	 * @param text: The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTextFormatting(String text) {
		
		if(text == null || text == "")
			return text;
		
		Matcher mat;Pattern pat;
		
		pat = Pattern.compile("'''''|'''|''");
		mat = pat.matcher(text);
		text = mat.replaceAll("");
		return text;
	}
	
	public static String parseComments(String text) {
		
		int nesting = -1;
		int  start = 0,end,offset = 0;
		boolean newnest = false;
		StringBuffer temptext = new StringBuffer(text);
		
		for (int i = 0; i < text.length(); ++i) {
			
			if (text.charAt(i) == '<' && text.charAt(i+1) == '!' && text.charAt(i+2) == '-' && text.charAt(i+3) == '-'){
				if (nesting == -1){
					newnest = true;
					start = i;
				}
				i += 3;
				nesting++;
				continue;
			}
			else if (text.charAt(i) == '-' && text.charAt(i+1) == '-' && text.charAt(i+2) == '>'){
				nesting--;
				i += 2;
			}
				
			if (nesting == -1 && newnest){
				newnest = false;
				end = i;
				//System.out.println(start);
				//System.out.println(end);
				temptext.delete(start-offset, end-offset+1);
				offset += end-start+1;
			}
		}
		
		//System.out.println(temptext.toString());
		//System.out.println("-----------------------------------------------");
		return temptext.toString();
	}
	
	
	/* TODO */
	/**
	 * Method to parse *any* HTML style tags like: <xyz ...> </xyz>
	 * For most cases, simply removing the tags should work.
	 * @param text: The text to be parsed
	 * @return The parsed text with the markup removed.
	 */
	public static String parseTagFormatting(String text) {
		
		if(text == null || text == "")
			return text;
		
		Matcher mat;Pattern pat;
		int index;
		
		if ( text == null )
			return null;
		
		/*
		 * Find a better way to do this. Maybe if can make use of StringUtils then this might be easier !
		 */
		text = text.replace("&amp;", "&");
		text = text.replace("&lt;", "<");
		text = text.replace("&gt;", ">");
		text = text.replace("&quot;", "\"");
		//System.out.println(text);
		// removing html mark up in case the tags are nested				
		text = parseComments(text);
		pat = Pattern.compile(" +\\<[\\w/][^\\>]*\\> +");
		mat = pat.matcher(text);
		text = mat.replaceAll(" ");
		mat.reset();
		pat = Pattern.compile("\\<[\\w/][^\\>]*\\> +");
		mat = pat.matcher(text);
		text = mat.replaceAll("");
		/*
		if( text.charAt(mat.start() - 1) != ' ' )
			text = mat.replaceAll(" ");
		else
			text = mat.replaceAll("");
		*/	
		mat.reset();
		pat = Pattern.compile(" +\\<[\\w/][^\\>]*\\>");
		mat = pat.matcher(text);
		text = mat.replaceAll("");
		/*
		if( text.charAt(mat.end() + 1) != ' ' )
			text = mat.replaceAll(" ");
		else
			text = mat.replaceAll("");
		*/
		mat.reset();
		pat = Pattern.compile("\\<[\\w/][^\\>]*\\>");
		mat = pat.matcher(text);
		text = mat.replaceAll("");
			
		//System.out.println(text);
		//System.out.println("-----------------------------------------------");
		return text;
	}
	
	/* TODO */
	/**
	 * Method to parse wikipedia templates. These are *any* {{xyz}} tags
	 * For most cases, simply removing the tags should work.
	 * @param text: The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTemplates(String text) {
		
		/*Pattern pat = Pattern.compile("\\{\\{[\\p{ASCII} | \\p{L}]*\\}\\}"); // Removing the {{ ... }}
		
		/*
		 * In order to remove the {{ }} alone [\\{\\{\\}\\}]
		 */
		//Matcher mat = pat.matcher(text);
		//text = mat.replaceAll("");
		//System.out.println(text);
		//System.out.println("-----------------------------------------------");
		
		if(text == null || text == "")
			return text;
		
		int nesting = -1;
		int  start = 0,end,offset = 0;
		boolean newnest = false;
		StringBuffer temptext = new StringBuffer(text);
		
		for (int i = 0; i < text.length(); ++i) {
			
			if (text.charAt(i) == '{' && text.charAt(i+1) == '{'){
				if (nesting == -1){
					newnest = true;
					start = i;	
				}
				i++;
				nesting++;
				continue;
			}
			else if (text.charAt(i) == '}' && text.charAt(i+1) == '}'){
				nesting--;
				i++;
			}
				
			if (nesting == -1 && newnest){
				newnest = false;
				end = i;
				//System.out.println(start);
				//System.out.println(end);
				temptext.delete(start-offset, end-offset+1);
				offset += end-start+1;
			}
		}
		
		//System.out.println(temptext.toString());
		//System.out.println("-----------------------------------------------");
		return temptext.toString();
	}
	
	public static String parseSpecialTemplates(String text) {
		
		if(text == null || text == "")
			return text;
		
		int nesting = -1;
		int  start = 0,end,offset = 0;
		boolean newnest = false;
		StringBuffer temptext = new StringBuffer(text);
		
		for (int i = 0; i < text.length(); ++i) {
			
			if (text.charAt(i) == '{' && text.charAt(i+1) == '|'){
				if (nesting == -1){
					newnest = true;
					start = i;	
				}
				i++;
				nesting++;
				continue;
			}
			else if (text.charAt(i) == '|' && text.charAt(i+1) == '}'){
				nesting--;
				i++;
			}
				
			if (nesting == -1 && newnest){
				newnest = false;
				end = i;
				temptext.delete(start-offset, end-offset+1);
				offset += end-start+1;
			}
		}
		return temptext.toString();
	}
	
	
	/* TODO */
	/**
	 * Method to parse links and URLs.
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Links_and_URLs
	 * @param text: The text to be parsed
	 * @return An array containing two elements as follows - 
	 *  The 0th element is the parsed text as visible to the user on the page
	 *  The 1st element is the link url
	 */
	public static String[] parseLinks(String text) {
		if ( text == null || text == "" ) {
			return new String[]{"",""};
		}
		/*
		
		*/
		StringBuffer part1, part2;
		String linkPart, buffer[], innerBuffer[], tempText, tempLink;
		Pattern pat; Matcher mat;
		int start, end, index, count1 = 0, count2 = 0, count3 = 0;
		
		if ( (index = text.indexOf("[[")) != -1 )
			start = index+2;
		else if ( (index = text.indexOf("[")) != -1 )
			start = index+1;
		else
			return new String[]{"",""};
		if ( (index = text.indexOf("]]")) != -1 )
			end = index;
		else if ( (index = text.indexOf("]")) != -1 )
			end = index;
		else
			return new String[]{"",""};
		
		linkPart = text.substring(start, end);
		buffer = linkPart.split("\\|");
		
		//System.out.println(buffer.length);
		
		if ( buffer.length == 2 ) {
			/*part1 = new StringBuffer(buffer[0]);
			part2 = new StringBuffer(buffer[1]);*/
			if ( buffer[0].matches("^[\\w][\\w ,]*") && buffer[1].matches("^[\\w][\\w ]*") ) {
				
				buffer[1] = text.replace(text.substring(start-2,end+2), buffer[1]);
				buffer[0] = buffer[0].replaceAll(" ", "_");
				buffer[0] = buffer[0].substring(0, 1).toUpperCase() + buffer[0].substring(1);
			}
			else if (!buffer[0].contains("://")){
				
				buffer[0] = "";
				buffer[1] = text.replace(text.substring(start-2,end+2), buffer[1]);
				
				
			}
			else {
				buffer[0] = "";
				buffer[1] = text.replace(text.substring(start-1,end+1), buffer[1]);
			}
			//System.out.println(buffer[0]+"~~"+buffer[1]);
			//count1++;
			return new String[]{buffer[1], buffer[0]};
		}
		else if ( buffer.length == 1 ) {
			
			if ( linkPart.contains("|") ) {
				tempLink = "";
				if ( buffer[0].contains("(") && buffer[0].contains(")") ) {
					tempLink = buffer[0].replaceAll(" ", "_");
					tempLink = tempLink.substring(0, 1).toUpperCase() + tempLink.substring(1);
					buffer[0] = buffer[0].replaceAll(" *\\([^\\)]*\\)", "");
				}
				if ( buffer[0].contains(",") ) {
					tempLink = buffer[0].replaceAll(" ", "_");
					tempLink = tempLink.substring(0, 1).toUpperCase() + tempLink.substring(1);
					buffer[0] = buffer[0].replaceAll(", .*", "");
				}
				if (buffer[0].contains(":") && !buffer[0].contains("#")) {
					
					buffer[0] = buffer[0].replaceFirst(".*?:", "");
					tempLink = "";	
				}
				if ( buffer[0].matches("[\\w ]") ) {
					tempLink = buffer[0].replaceAll(" ", "_");
					tempLink = tempLink.substring(0, 1).toUpperCase() + tempLink.substring(1);
				}
				return new String[]{buffer[0],tempLink};
			}
			else {
				
				if ( buffer[0].contains(":") ) {
					
					if (buffer[0].contains("Category:") && !buffer[0].contains("://")) {
						innerBuffer = buffer[0].split(":");
						if ( innerBuffer.length == 2 ) 
							buffer[0] = innerBuffer[1];
						else if ( innerBuffer.length == 3 ) 
							buffer[0] = innerBuffer[1]+":"+innerBuffer[2];
						tempText = text.replace(text.substring(start-2,end+2), buffer[0]);
						return new String[]{tempText, ""};
					}
					else if ( buffer[0].matches("^[\\w][\\w ]*:[\\w ]*")) {
						tempText = text.replace(text.substring(start-2,end+2), buffer[0]);
						return new String[]{tempText, ""};
					}
					else if ( buffer[0].matches("^[\\w][\\w]*://.*") ){
						if ( buffer[0].contains(" ") ) {
							innerBuffer = buffer[0].split(" ",2);
							tempText = text.replace(text.substring(start-1,end+1), innerBuffer[1]);
							return new String[]{tempText, ""};
						}
						else {
							return new String[]{"", ""};
						}
							
					}
					else {
						if (buffer[0].matches("^[\\w][\\w ]*:[^\\.]*"))
							tempText = text.replace(text.substring(start-2,end+2), buffer[0]);
						else
							tempText = "";
						return new String[]{tempText, ""};
					}
				}
				else if (buffer[0].matches("^[\\w\\p{L}\u201C\"][^:]*")) {
					tempText = text.replace(text.substring(start-2,end+2), buffer[0]);
					buffer[0] = buffer[0].replaceAll(" ", "_");
					buffer[0] = buffer[0].substring(0, 1).toUpperCase() + buffer[0].substring(1);
					if ( tempText.contains("<nowiki />")) {
						tempText = tempText.replace("<nowiki />", "");
					}
					return new String[]{tempText, buffer[0]};
				}
			}
			
			//count2++;
		}
		else if ( buffer.length >= 2) {
			//System.out.println(buffer[buffer.length-1]);
			//count3++;
			return new String[]{buffer[buffer.length-1],""};
		}
		
		/*
		for ( int i = start; i <= end; ++i ) {
		}
		*/
		//System.out.println(count1+" "+count2+" "+count3);
		return null;
		
	}
	
	
	
}
