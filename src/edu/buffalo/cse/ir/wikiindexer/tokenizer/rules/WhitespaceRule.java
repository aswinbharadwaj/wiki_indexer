package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.Properties;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.WHITESPACE)
public class WhitespaceRule implements TokenizerRule {
	
	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			stream.reset();
			String buffer[];
			String token;
			//System.out.println("Before "+stream.getAllTokens());
			while (stream.hasNext()) {
				token = stream.next();
				if (token.matches("\\s*")){
					stream.previous();
					stream.remove();
					continue;
				}
				buffer = token.split("\\s\\s*");
				stream.previous();
				stream.set(buffer);
				stream.next();
			}
			//System.out.println("After "+stream.getAllTokens());
		}
		
	}
	
}

