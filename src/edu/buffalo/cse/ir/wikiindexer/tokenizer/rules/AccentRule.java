package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;





import edu.buffalo.cse.ir.wikiindexer.FileUtil;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.ACCENTS)
public class AccentRule implements TokenizerRule{

	Properties props;
	Map<String, String> map;
	
	public AccentRule (Properties props) {
		this.props = props;
		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileReader(FileUtil.getRootFilesFolder(props)+"amap.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map = new HashMap<String, String>();

        while (scanner.hasNextLine()) {
            String[] columns = scanner.nextLine().split(" ");
            map.put(columns[0], columns[1]);
        }
        scanner.close();
	}
	
	
	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		//System.out.println("In Accent");
		if ( stream != null ) {
			stream.reset();
	        String token;	//token for replacement
	        Pattern pat;
	        pat = Pattern.compile(".*?\\W.*?");
	        while (stream.hasNext()) { 
				
				token = stream.next();
				if (token.matches(pat.pattern())) {
					for (String left: map.keySet()) {
					
						token = token.replaceAll(left, map.get(left));
					}
					stream.previous();
					stream.set(token);
					stream.next();
	        	}
					
	        }
		}
	}
}
