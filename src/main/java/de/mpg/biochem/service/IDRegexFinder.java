package de.mpg.biochem.service;

import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IDRegexFinder {
	
	private final Pattern uniprotPattern = Pattern.compile("[A-Z][0-9][A-Z0-9]{3}[0-9]?");
	private final Pattern ncbiGiPattern = Pattern.compile("[0-9]{6,}");
	private final Pattern ensemblPattern = Pattern.compile("ENS[A-Z0-9]{10,17}");
	private final Pattern refseqPattern = Pattern.compile("(NC|AC|NG|NT|NW|NZ|NM|NR|XM|XR|NP|AP|XP|YP|ZP)_[0-9]+");
	
	private TreeSet<String> uniprot;
	private TreeSet<String> ncbiGi;
	private TreeSet<String> ensembl;
	private TreeSet<String> refseq;
	
	private TreeSet<String> all;
	
	private String matcher;
	
	public IDRegexFinder(String matcher){
		this.matcher = matcher;
		
		ensembl = find(ensemblPattern);
		refseq = find(refseqPattern);
		uniprot = find(uniprotPattern);
		ncbiGi = find(ncbiGiPattern);
	}
	
	public TreeSet<String> getUniprot() {
		return uniprot;
	}

	public TreeSet<String> getNcbiGi() {
		return ncbiGi;
	}

	public TreeSet<String> getEnsembl() {
		return ensembl;
	}

	public TreeSet<String> getRefseq() {
		return refseq;
	}
	
	public TreeSet<String> getAll() {
		if(all == null){
			all = new TreeSet<String>();
			all.addAll(ensembl);
			all.addAll(refseq);
			all.addAll(uniprot);
			all.addAll(ncbiGi);
		}
		
		return all;
	}

	private TreeSet<String> find(Pattern pattern){
		TreeSet<String> set = new TreeSet<String>();
		Matcher m = pattern.matcher(matcher);
		while (m.find()) {
			String group = m.group();
			set.add(group);
			matcher = matcher.replace(group, "-");
		}
		return set;
	}
}
