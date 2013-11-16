package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.APOSTROPHE)
public class ApostropheRule implements TokenizerRule {
	
	Pattern p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12,p13,p14,p15,p16,p17,p18,p19,p20,p21,p22;
	
	public ApostropheRule(){
		p1 = Pattern.compile(".*[\\s']+[s]+.*");
		p2 = Pattern.compile("[\\s'][s]");
		p3 = Pattern.compile(".*[\\s']+.*");
		p4 = Pattern.compile(".*(n t).*");
		p5 = Pattern.compile("(n t)");
		p6 = Pattern.compile("^(wo)$");
		p7 = Pattern.compile("^(sha)$");
		p8 = Pattern.compile(".*( m)$");
		p9 = Pattern.compile("( m)");
		p10 = Pattern.compile(".*( re)$");
		p11 = Pattern.compile("( re)");
		p12 = Pattern.compile(".*( ve)$");
		p13 = Pattern.compile("( ve)");
		p14 = Pattern.compile(".*( d)$");
		p15 = Pattern.compile("( d)");
		p16 = Pattern.compile(".*( ll)$");
		p17 = Pattern.compile("( ll)");
		p18 = Pattern.compile(".*( em)$");
		p19 = Pattern.compile("^( em)");
		p20 = Pattern.compile("(\\s).+");
		p21 = Pattern.compile(".+(\\s)$");
		p22 = Pattern.compile(".*f(\\s).+");
	}

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		boolean appendNotFlag = false;
		boolean appendAmFlag = false;
		boolean appendAreFlag = false;
		boolean appendHaveFlag = false;
		boolean appendWouldFlag = false;
		boolean appendWillFlag = false;
		if (stream != null) {
			stream.reset();
			while(stream.hasNext()){
				String tempString = stream.next();
				if  (tempString.contains("\'")){
					
					if (tempString.matches(p1.pattern())){
						tempString = tempString.replaceAll(p2.pattern(), "");
						stream.previous();
						stream.set(tempString);
						stream.next();
						if(tempString.equals("let") || tempString.equals("Let")){
							stream.append("us");
							stream.next();
						}
					}else if(tempString.matches(p3.pattern())){
						tempString = tempString.replaceAll("'", " ");
						tempString = tempString.replaceAll("  ", " ");
						if(tempString.matches(p4.pattern())){
							tempString = tempString.replaceAll(p5.pattern(), "");
							appendNotFlag = true;
							tempString = tempString.replaceAll(p6.pattern(), "will");
							tempString = tempString.replaceAll(p7.pattern(), "shall");
						}
						else if(tempString.matches(p8.pattern())){
							tempString = tempString.replaceAll(p9.pattern(), "");
							appendAmFlag = true;
						}
						else if(tempString.matches(p10.pattern())){
							tempString = tempString.replaceAll(p11.pattern(), "");
							appendAreFlag = true;
						}
						else if(tempString.matches(p12.pattern())){
							tempString = tempString.replaceAll(p13.pattern(), "");
							appendHaveFlag = true;
						}
						else if(tempString.matches(p14.pattern())){
							tempString = tempString.replaceAll(p15.pattern(), "");
							appendWouldFlag = true;
						}
						else if(tempString.matches(p16.pattern())){
							tempString = tempString.replaceAll(p17.pattern(), "");
							appendWillFlag = true;
						}else if(tempString.matches(p18.pattern())){
							tempString = tempString.replaceAll(p19.pattern(), "them");
						}else if(tempString.matches(p20.pattern())){
							tempString = tempString.replaceAll(" ", "");
						}else if(tempString.matches(p21.pattern())){
							tempString = tempString.replaceAll(" ", "");
						}else if(tempString.matches(p22.pattern())){
							tempString = tempString.replaceAll(" ", "");
						}
						stream.previous();
						stream.set(tempString);
						stream.next();
						if(appendNotFlag == true){
							stream.append("not");
							stream.next();
							appendNotFlag = false;
						}else if(appendAmFlag == true){
							stream.append("am");
							stream.next();
							appendAmFlag = false;
						}else if(appendAreFlag == true){
							stream.append("are");
							stream.next();
							appendAreFlag = false;
						}else if(appendHaveFlag == true){
							stream.append("have");
							stream.next();
							appendHaveFlag = false;
						}else if(appendWouldFlag == true){
							stream.append("would");
							stream.next();
							appendWouldFlag = false;
						}else if(appendWillFlag == true){
							stream.append("will");
							stream.next();
							appendWillFlag = false;
						}
					}
				}
			}
		}
		
	}

}
