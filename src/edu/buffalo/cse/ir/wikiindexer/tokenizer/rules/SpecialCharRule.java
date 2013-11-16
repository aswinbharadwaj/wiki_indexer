package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.SPECIALCHARS)
public class SpecialCharRule implements TokenizerRule {

	Pattern pat;
	public SpecialCharRule() {
		pat = Pattern.compile("[\"~\\(\\)@#\\$%\\^\\&\\*\\=\\+:;\\<\\>\\|_/\\\\]");
	}
	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		if (stream != null) {
			stream.reset();
			String buffer[],token;
			
			//System.out.println("Before "+stream.getAllTokens());
			while (stream.hasNext()) { 
				
				token = stream.next();
				buffer = token.split(pat.pattern());
				
				stream.previous();
				if(buffer.length == 0) {
					stream.remove();
					continue;
				}
				stream.set(buffer);
				
				stream.next();
		
			}
			//System.out.println("After "+stream.getAllTokens());
		
		}
	}

}
