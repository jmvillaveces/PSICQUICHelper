package de.mpg.biochem.batch.io;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.springframework.batch.item.ItemProcessor;

public class UPIndexProcessor implements ItemProcessor<String[], Document>{
	
	// UniProtKB-AC, RefSeq, GI, Ensembl
	private int[] idColumns = new int[]{2, 3, 4, 18};
	
	//Ends with dot and number
	private String splitPattern = "\\.|-\\d$";
	
	public UPIndexProcessor(){}
	
	@Override
	public Document process(String[] line) throws Exception {
		
		String uniprot = line[0].split(splitPattern)[0];
		
		List<String> altIds = new ArrayList<String>();
		altIds.add(uniprot);
		
		for(int i : idColumns){
			processField(i, line, altIds);
		}
		
		Document doc = new Document();
		doc.add(new StringField("id", uniprot, Field.Store.YES));
		
		for(String altId : altIds){
			altId = altId.split(splitPattern)[0];
			doc.add(new StringField("altId", altId, Field.Store.YES));
		}
		
		return doc;
	}
	
	private void processField(int index, String[] array, List<String> altIds){
		if(array.length > index){
			String tmp = array[index];
			
			String[] tmpArr = tmp.split(";");
			for(String s : tmpArr){
				altIds.add(s.trim());
			}
		}
	}
}
