package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.STOPWORDS)
public class StopWordsRule implements TokenizerRule {
	
	Properties props;
	List<String> stopwords;
	
	public StopWordsRule(Properties props) {
		// TODO Auto-generated constructor stub
		this.props = props;
		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileReader(FileUtil.getRootFilesFolder(props)+"stopwords.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stopwords = new LinkedList<String>();
		scanner.useDelimiter(",");
		while (scanner.hasNext())
			stopwords.add(scanner.next());
	    scanner.close();
	}
	
	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		if ( stream != null ) {
			stream.reset();
			//System.out.println("Before "+stream.getAllTokens());
		    String token;
		    while (stream.hasNext()) { 
				token = stream.next();
				for (String stopword: stopwords) 
					if ( token.toLowerCase().equals(stopword) ) {
						stream.previous();
						stream.remove();
						break;
					}
			}
		    //System.out.println("After "+stream.getAllTokens());	
		}
	}
}
