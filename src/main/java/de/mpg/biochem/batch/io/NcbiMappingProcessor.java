package de.mpg.biochem.batch.io;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.batch.item.ItemProcessor;

public class NcbiMappingProcessor implements ItemProcessor<String[],String[]>{

	//Ends with dot and number
	private Pattern dotNumber = Pattern.compile("^.+\\.\\d$");

	@Override
	public String[] process(String[] item) throws Exception {
		
		List<String> processed = new ArrayList<String>();
		for(int i=0; i<item.length; i++){
			String id = item[i];
			
			if(!id.equals("") && !id.equals("-")){
				Matcher matcher = dotNumber.matcher(id);
				if(matcher.matches())
					id = id.substring(0, id.indexOf("."));
				
				processed.add(id);
			}
		}
		return processed.toArray(new String[0]);
	}

}
