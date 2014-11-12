package de.mpg.biochem.batch.io;

import java.io.File;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;


public class DocumentWriter implements ItemStreamWriter<Document> {

	private Version lVersion = Version.LATEST;
	private Analyzer analyzer;
	private IndexWriter indexWriter;
	private String path;
	
	public DocumentWriter(String path){
		this.path = path;
	}

	@Override
	public void write(List<? extends Document> docs) throws Exception {
		for(Document doc : docs){
			indexWriter.addDocument(doc);
		}
		indexWriter.commit();
	}
	
	@Override
	public void close() throws ItemStreamException {
		try {
			indexWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void open(ExecutionContext ctx) throws ItemStreamException {
		analyzer = new StandardAnalyzer();
		IndexWriterConfig conf = new IndexWriterConfig(lVersion, analyzer);
		
		try {
			indexWriter = new IndexWriter(FSDirectory.open(new File(path)), conf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(ExecutionContext ctx) throws ItemStreamException {}
}
