package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.PUNCTUATION)
public class PunctuationRule implements TokenizerRule{

	Pattern pat1,pat2;
	public PunctuationRule(){
		pat1 = Pattern.compile("'[^']*'");
		pat2 = Pattern.compile("(\\w+)\\p{Punct}+(\\s|$)");
	}
	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		if (stream != null) {
					stream.reset();
					String tempString = null;
					boolean flagQoute = false;
					while (stream.hasNext()) { 
						tempString = stream.next();

						if(tempString.matches(pat1.pattern())){	
							flagQoute = true;
						}
						tempString = tempString.replaceAll(pat2.pattern(), "$1$2");
						if(flagQoute == true){
							tempString = tempString + "'";
							flagQoute = false;
						}
						stream.previous();
						stream.set(tempString);
						stream.next();
					}
				}
		
		}

}
