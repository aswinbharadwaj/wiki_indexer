package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.HYPHEN)
public class HyphenRule implements TokenizerRule {

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		
		
		if (stream != null) {
			stream.reset();
			
			while(stream.hasNext()){
				
			String tempString = stream.next();
			tempString = tempString.replaceAll("\\p{Pd}", "-");
			if(tempString.matches("^([-]+| [-]* )$")){
				stream.previous();
				stream.remove();
			}
			else if(tempString.matches("[a-zA-z]+[-]+[a-zA-z]+")){
					
					tempString = tempString.replaceAll("[-]", " ");
					stream.previous();
					stream.set(tempString);
			}else if(tempString.matches("[a-zA-Z0-9]+[-]+$")||tempString.matches("^[-]+[a-zA-Z0-9]+")){
				tempString = tempString.replaceAll("[-]", "");
				stream.previous();
				stream.set(tempString);
			}else if(tempString.matches("^[-]+[a-zA-Z]+[-]+[a-zA-Z]+")){
				tempString = tempString.replaceFirst("[-]", "");
				tempString = tempString.replaceAll("[-]", " ");
				stream.previous();
				stream.set(tempString);
			}
			}
			stream.next();
			}
			
		
	}
}