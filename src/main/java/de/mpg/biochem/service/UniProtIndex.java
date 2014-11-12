package de.mpg.biochem.service;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class UniProtIndex {

	private Directory directory;
	private IndexSearcher searcher;
	
	public UniProtIndex(String path) throws IOException{
		directory = FSDirectory.open(new File(path));
		searcher = new IndexSearcher(DirectoryReader.open(directory));
	}
	
	public TreeSet<String> search(String accession, String taxId) throws IOException, ParseException{
		Query query = new TermQuery(new Term("altId", accession));
		
		TreeSet<String> ids = new TreeSet<String>();
		TopScoreDocCollector collector = TopScoreDocCollector.create(1, true);
		searcher.search(query, collector);
		
		if(collector.getTotalHits() > 0){
			
			collector = TopScoreDocCollector.create(collector.getTotalHits(), true);
			searcher.search(query, collector);
			
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			
			for(int i=0;i<hits.length;++i) {
				int docId = hits[i].doc;
			    Document doc = searcher.doc(docId);
				String docTaxId = doc.get("taxId"), id = doc.get("id");
				
				if(taxId == null || taxId.equals(docTaxId)) {
					ids.add(id);
				}		
			}
		}
		return ids;
	}
	
	public TreeSet<String> search(String accession) throws ParseException, IOException{

		Query query = new TermQuery(new Term("altId", accession));
		
		TreeSet<String> ids = new TreeSet<String>();
		TopScoreDocCollector collector = TopScoreDocCollector.create(1, true);
		searcher.search(query, collector);
		
		if(collector.getTotalHits() > 0){
			
			collector = TopScoreDocCollector.create(collector.getTotalHits(), true);
			searcher.search(query, collector);
			
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			
			for(int i=0;i<hits.length;++i) {
			    int docId = hits[i].doc;
			    Document d = searcher.doc(docId);
			    ids.add(d.get("id"));
			}
		}
		return ids;
	}
}
