package de.mpg.biochem.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

public class UniprotMappingIndexWriter implements ItemStreamWriter<String[]>{

	private IndexWriter indexWriter;
	private Version lVersion = Version.LUCENE_35;
	private String path;
	private int[] idColumns = new int[]{1, 2, 3, 4, 11, 12, 15, 17, 18, 19};
	
	//Ends with dot and number
	private String splitPattern = "\\.|-\\d$";
	private Pattern dotNumber = Pattern.compile("^.+\\.\\d$");
	
	public UniprotMappingIndexWriter(String path){
		this.path = path;
	}
	
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		IndexWriterConfig conf = new IndexWriterConfig(lVersion, new StandardAnalyzer(lVersion));
		
		try {
			indexWriter = new IndexWriter(FSDirectory.open(new File(path)), conf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		
	}

	@Override
	public void close() throws ItemStreamException {
		try {
			indexWriter.commit();
			indexWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(List<? extends String[]> items) throws Exception {
		for(String line[] : items){
			
			String uniprot = line[0].split(splitPattern)[0];
			List<String> altIds = new ArrayList<String>();
			altIds.add(uniprot);
			
			for(int i : idColumns){
				processField(i, line, altIds);
			}
			
			Document doc = new Document();
			doc.add(new Field("id", uniprot, Field.Store.YES, Field.Index.NOT_ANALYZED));
			
			for(String altId : altIds){
				altId = altId.split(splitPattern)[0];
				doc.add(new Field("altId", altId, Field.Store.YES, Field.Index.NOT_ANALYZED));
			}
			doc.add(new Field("taxId", line[13].trim(), Field.Store.YES, Field.Index.NOT_ANALYZED));
			
			indexWriter.addDocument(doc);
		}
		indexWriter.commit();
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
