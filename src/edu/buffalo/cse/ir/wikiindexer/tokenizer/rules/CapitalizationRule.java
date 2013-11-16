package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.CAPITALIZATION)
public class CapitalizationRule implements TokenizerRule {

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		if (stream != null) {
			//System.out.println("Before " + stream.getAllTokens());
					stream.reset();
					String tempString;
					while (stream.hasNext()) {
						String prev = stream.previous();
						if(prev != null){
							stream.next();
						}
						tempString = stream.next();
						if(prev == null){
							tempString = tempString.toLowerCase();
							stream.previous();
							stream.set(tempString);
						}else if(prev.contains(".") && tempString.matches("^[A-Z][a-z]*")){
							tempString = tempString.toLowerCase();
							stream.previous();
							stream.set(tempString);
						}
						stream.next();
					}
				}
		//System.out.println("After " + stream.getAllTokens());
		
		}

}
