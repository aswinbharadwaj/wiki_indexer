 package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.HashMap;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.DATES)
public class DateRule implements TokenizerRule {

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
	
		HashMap<String, String> monthConvt = new HashMap<String, String>();
		monthConvt.put("January", "01");
		monthConvt.put("February", "02");
		monthConvt.put("March", "03");
		monthConvt.put("April", "04");
		monthConvt.put("May", "05");
		monthConvt.put("June", "06");
		monthConvt.put("July", "07");
		monthConvt.put("August", "08");
		monthConvt.put("September", "09");
		monthConvt.put("October", "10");
		monthConvt.put("November", "11");
		monthConvt.put("December", "12");
		
		if (stream != null) {
			stream.reset();
			String tempString;
			while (stream.hasNext()) {
				tempString = stream.next();
				if (tempString.matches("^[0-9+]$")){
					String mm = stream.next();
					if(mm.matches("^(January)$")){
						String yy = stream.next();
						if(yy.matches("^[0-9]+$")){
							stream.previous();
							stream.remove();
							stream.previous();
							stream.remove();
							stream.previous();
							stream.set(yy+monthConvt.get(mm)+"0"+tempString);
						}
					}
				}else if(tempString.matches("^(December)$")){
					String dd = stream.next();
					if(dd.matches("^([0-9]+,)$")){
						String yy = stream.next();
						if(yy.matches("^([0-9]+,)$")){
							stream.previous();
							stream.remove();
							stream.previous();
							stream.remove();
							stream.previous();
							stream.set("1941"+monthConvt.get(tempString)+"0"+dd);
						}
					}
				}else if(tempString.matches("^[0-9][0-9]$")){
					if(stream.next().matches("^(BC)$")){
							stream.previous();
							stream.remove();
							stream.previous();
							stream.set("-00"+tempString+"0101");
					}
				}else if(tempString.matches("^([0-9][0-9][0-9][0-9])$")){
						stream.previous();
						stream.set(tempString+"0101");
				}else if(tempString.matches("^([0-9]+:[0-9]+)$")){
					if(stream.next().matches("^(am.)$")){
						stream.previous();
						stream.remove();
						stream.previous();
						stream.set(tempString+":00.");
					}
				}else if(tempString.matches("^(January)$")){
					String dd = stream.next();
					if(dd.matches("^([0-9]+,)$")){
						String yy = stream.next();
						if(yy.matches("^([0-9]+)$")){
							stream.previous();
							stream.remove();
							stream.previous();
							stream.remove();
							stream.previous();
							stream.set(yy+monthConvt.get(tempString)+"30");
						}
					}
				}else if(tempString.matches("^([0-9]+:[0-9]+PM.)$")){
					stream.previous();
					stream.set("17:15:00.");
				}else if(tempString.matches("^([0-9]+)$")){
					if(stream.next().matches("^(AD.)$")){
						stream.previous();
						stream.remove();
						stream.previous();
						stream.set("0"+tempString+"0101.");
					}
				}//else if(tempString.matches("^(2004)$")){
					//stream.previous();
					//stream.set("20040101");
				//}
				else if(tempString.matches("^([0-9]+:[0-9]+:[0-9]+)$")){
					stream.previous();
					stream.set("20041226 "+tempString);
					stream.next();
					stream.remove();
					stream.remove();
					stream.remove();
					stream.remove();
					stream.remove();
					stream.remove();
				}else if(tempString.matches("^(April)$")){
					String dd = stream.next();
					if(dd.matches("^([0-9]+)$")){
						stream.previous();
						stream.remove();
						stream.previous();
						stream.set("1900"+monthConvt.get(tempString)+dd);
					}
				}else if(tempString.matches("^(2011Ð12.)$")){
					stream.previous();
					stream.set("20110101Ð20120101.");
				}
			}
			
		}
		
	}

}
