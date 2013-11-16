package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.Properties;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.DELIM)
public class DelimiterRule implements TokenizerRule{

	Properties props;
	
	public DelimiterRule (Properties props) {
		this.props = props;
	}
	
	
	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		
		if ( stream != null ) {
			stream.reset();
			
		    String token;
		    String buffer[];
		    Pattern pat;
		    pat = Pattern.compile("[^'\\-\\w\\,\\!~\\(\\)@#\\$%\\^\\&\\*\\=\\+:;\\<\\>\\|_/\\\\\\s]");
		    while (stream.hasNext()) { 
				token = stream.next();
				buffer = token.split(pat.pattern());
				if(buffer.length == 0) {
					stream.remove();
					continue;
				}
				stream.set(buffer);
				stream.next();
			}
			
		}
		
	}
}