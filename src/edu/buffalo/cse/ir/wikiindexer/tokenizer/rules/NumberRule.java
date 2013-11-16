package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.NUMBERS)
public class NumberRule implements TokenizerRule{

	Pattern pat;
	public NumberRule() {
		pat = Pattern.compile("\\d\\d*[,\\.]\\d\\d*|\\d\\d*");
	}
	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		if ( stream != null) {
			stream.reset();
			String token;
			while (stream.hasNext()) { 
					token = stream.next();
			        token =	token.replaceAll(pat.pattern(), "");
					if ( token.equals("") ) {
						stream.previous();
						stream.remove();
						continue;
					}
					stream.previous();
					stream.set(token);
					stream.next();
			 }
		}
			
		
	}

}
