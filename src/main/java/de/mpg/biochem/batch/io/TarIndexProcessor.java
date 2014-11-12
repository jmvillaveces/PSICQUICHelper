package de.mpg.biochem.batch.io;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.springframework.batch.item.ItemProcessor;

public class TarIndexProcessor implements ItemProcessor<String[], Document>{
	
	//Ends with dot and number
	private String splitPattern = "\\.|-\\d$";
	
	@Override
	public Document process(String[] line) throws Exception {
		
		String uniprot = line[0].split(splitPattern)[0];
		
		List<String> altIds = new ArrayList<String>();
		altIds.add(uniprot);
		altIds.add(line[1]);
		
		Document doc = new Document();
		doc.add(new StringField("id", uniprot, Field.Store.YES));
		
		for(String altId : altIds){
			altId = altId.split(splitPattern)[0];
			doc.add(new StringField("altId", altId, Field.Store.YES));
		}
		return doc;
	}

}
