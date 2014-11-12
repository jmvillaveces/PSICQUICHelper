package de.mpg.biochem.batch.io;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.springframework.batch.item.ItemProcessor;

import de.mpg.biochem.service.UniProtIndex;

public class IdProcessor implements ItemProcessor<String [], String[]>{

	private UniProtIndex uniprot;
	
	public IdProcessor(String indexPath) throws IOException{
		uniprot = new UniProtIndex(indexPath);
	}
	
	@Override
	public String[] process(String[] ids) throws Exception {
		String[] arr = uniprot.search(ids[0]).toArray(new String[0]);
		String str = StringUtils.join(arr, "|");
		return new String[] {ids[0], str};
	}
}
