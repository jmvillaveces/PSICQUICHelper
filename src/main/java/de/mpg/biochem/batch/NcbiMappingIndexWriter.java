package de.mpg.biochem.batch;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

public class NcbiMappingIndexWriter implements ItemStreamWriter<String[]>{

	private IndexWriter indexWriter;
	private Version lVersion = Version.LUCENE_35;
	private String path;
	
	//Uniprot regex
	private Pattern uniprotPattern = Pattern.compile("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]");
	private String splitPattern = "\\.|-\\d$";
	
	public NcbiMappingIndexWriter(String path){
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
	public void write(List<? extends String[]> lines) throws Exception {
		for(int i=0; i<lines.size(); i++){
			addMapping(lines.get(i));
		}
		indexWriter.commit();
	}
	
	private void addMapping(String[] line) throws CorruptIndexException, IOException{
		
		String protAccession = line[5].split(splitPattern)[0];
		Matcher matcher = uniprotPattern.matcher(protAccession);
		if(matcher.matches()){
			
			String taxId = line[0];
			String ncbiGeneId = line[1];
			
			Document doc = new Document();
			doc.add(new Field("taxId", taxId, Field.Store.YES, Field.Index.NOT_ANALYZED));
			
			doc.add(new Field("id", protAccession, Field.Store.YES, Field.Index.NOT_ANALYZED));
			
			doc.add(new Field("altId", protAccession, Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field("altId", ncbiGeneId, Field.Store.YES, Field.Index.NOT_ANALYZED));
			
			
			//geneSymbol
			if(line.length>15)
				doc.add(new Field("altId", line[15].split(splitPattern)[0], Field.Store.YES, Field.Index.NOT_ANALYZED));
			
			indexWriter.addDocument(doc);
		}
	}

}
