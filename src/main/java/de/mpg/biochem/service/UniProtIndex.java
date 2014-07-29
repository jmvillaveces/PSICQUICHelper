package de.mpg.biochem.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class UniProtIndex {

	private Directory directory;
	private IndexSearcher searcher;
	
	public UniProtIndex(String path) throws IOException{
		directory = FSDirectory.open(new File(path));
		searcher = new IndexSearcher(IndexReader.open(directory));
	}
	
	public String search(String accession) throws CorruptIndexException, IOException{
		Query query = new TermQuery(new Term("altId", accession));
		TopDocs hits = searcher.search(query, 1);
		if(hits.totalHits>0){
			Document doc = searcher.doc(hits.scoreDocs[0].doc);
			//searcher.close();
			return doc.get("id");
		}
		//searcher.close();
		return "";
	}
	
	public String search(String[] accessions) throws CorruptIndexException, IOException, ParseException{
		List<String> lst = new ArrayList<String>();
		for(String acc : accessions){
			String tmp = addQuotes(acc);
			if(tmp != null)
				lst.add(tmp);
		}
		
		String queryStr = StringUtils.join(lst.toArray(new String[0])," OR ");
		Query query = new QueryParser(Version.LUCENE_35, "altId", new StandardAnalyzer(Version.LUCENE_35)).parse(QueryParser.escape(queryStr));
		
		TopDocs hits = searcher.search(query, 1);
		if(hits.totalHits>0){
			Document doc = searcher.doc(hits.scoreDocs[0].doc);
			return doc.get("id");
		}
		return "";
	}
	
	public TreeSet<String> smartSearch(String[] accessions, String taxId) throws CorruptIndexException, IOException, ParseException{
		List<String> lst = new ArrayList<String>();
		for(String acc : accessions){
			String tmp = addQuotes(acc);
			if(tmp != null)
				lst.add(tmp);
		}
		
		String queryStr = StringUtils.join(lst.toArray(new String[0])," OR ");
		Query query = new QueryParser(Version.LUCENE_35, "altId", new StandardAnalyzer(Version.LUCENE_35)).parse(QueryParser.escape(queryStr));
		
		TreeSet<String> ids = new TreeSet<String>();
		TreeSet<String> taxIds = new TreeSet<String>();
		TopDocs hits = searcher.search(query, 1);
		if(hits.totalHits>0){
			
			hits = searcher.search(query, hits.totalHits);
			for(int i=0; i<hits.totalHits; i++){
				Document doc = searcher.doc(hits.scoreDocs[i].doc);
				String docTaxId = doc.get("taxId"), docId = doc.get("id");
				
				if(taxId != null && taxId.equals(docTaxId))
					taxIds.add(docId);
					
				ids.add(docId);
			}
		}
		
		if(taxIds.size()>0)
			return taxIds;
		
		return ids;
	}
	
	private String addQuotes(String str) {
		str = str.trim();
		if(!isEmpty(str))
		    return "\""+ str+"\"";
		
		return null;
	}
	
	private boolean isEmpty(String str){
		return !(str.length()>0);
	}
}
