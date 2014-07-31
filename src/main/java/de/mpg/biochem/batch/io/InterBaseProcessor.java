package de.mpg.biochem.batch.io;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.springframework.batch.item.ItemProcessor;
import org.xml.sax.SAXException;

import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.CrossReference;
import psidev.psi.mi.tab.model.CrossReferenceImpl;
import psidev.psi.mi.tab.model.Interactor;
import de.mpg.biochem.service.IDRegexFinder;
import de.mpg.biochem.service.UniProtIndex;

public class InterBaseProcessor implements ItemProcessor<BinaryInteraction, BinaryInteraction> {

	private List<String> servicesToSkip;
	
	private UniProtIndex uniprot;
	private File notMapped;
	
	private Logger logger = Logger.getLogger(InterBaseProcessor.class);
	
	private Pattern uniprotPattern = Pattern.compile("[A-Z][0-9][A-Z0-9]{3}[0-9]?");
	
	public InterBaseProcessor(String indexPath, String filePath) throws IOException{
		uniprot = new UniProtIndex(indexPath);
		
		notMapped = new File(filePath);
		if(notMapped.exists())
			notMapped.delete();
	}

	@Override
	public BinaryInteraction process(BinaryInteraction interaction) throws Exception {
		
		CrossReference cr = (CrossReference) interaction.getSourceDatabases().get(interaction.getSourceDatabases().size() -1);
		
		//Check if interaction should be analyzed
		if(!servicesToSkip.contains(cr.getIdentifier())){
			processInteractor(interaction.getInteractorA());
			processInteractor(interaction.getInteractorB());
		}
		
		return interaction;
	}
	
	public void processInteractor(Interactor interactor) throws MalformedURLException, SAXException, IOException, ParseException, InterruptedException {
		
		//In case is a molecule interacts with itself
		if(interactor != null && interactor.getIdentifiers().size() > 0){
			
			List<CrossReference> ids = new ArrayList<CrossReference>();
			ids.addAll(interactor.getIdentifiers());
			ids.addAll(interactor.getAlternativeIdentifiers());
			
			String taxId = interactor.getOrganism().getTaxid();
			
			CrossReference accession = getBestGuessId(interactor);
			Matcher matcher = uniprotPattern.matcher(accession.getIdentifier());
			if(!matcher.matches())
				FileUtils.writeStringToFile(notMapped, interactor.toString()+"\n", true);
				
			List<CrossReference> identifiers = interactor.getIdentifiers();
			interactor.getAlternativeIdentifiers().addAll(identifiers);
			identifiers.clear();
			identifiers.add(accession);
		}
	}
	
	private CrossReference getBestGuessId(Interactor interactor) throws CorruptIndexException, IOException, ParseException{
		
		//MainIds
		List<CrossReference> mainIds = interactor.getIdentifiers();
		
		//Alt Ids
		List<CrossReference> ids = new ArrayList<CrossReference>();
		ids.addAll(mainIds);
		ids.addAll(interactor.getAlternativeIdentifiers());

		String taxId = interactor.getOrganism().getTaxid();
		
		//Default bestGuess 
		CrossReference bestGuess = mainIds.get(0);
		
		TreeSet<String> accessions = uniprot.smartSearch(getIdsStringArray(ids), taxId);
		if(accessions.size() == 1){
			return  new CrossReferenceImpl("uniprotkb", accessions.first());
		}else{
			IDRegexFinder rFinder = new IDRegexFinder(StringUtils.join(mainIds, "$"));
			
			String mapped = null;
			if(rFinder.getUniprot().size() > 0){
				mapped = map(rFinder.getUniprot().toArray(new String[0]), taxId);
				bestGuess = (mapped != null) ? new CrossReferenceImpl("uniprotkb", mapped) : new CrossReferenceImpl("uniprotkb", rFinder.getUniprot().first());
			}else if(rFinder.getRefseq().size() > 0){
				mapped = map(rFinder.getRefseq().toArray(new String[0]), taxId);
				bestGuess = (mapped != null) ? new CrossReferenceImpl("uniprotkb", mapped) : new CrossReferenceImpl("refseq", rFinder.getRefseq().first());
			}else if(rFinder.getNcbiGi().size() > 0){
				mapped = map(rFinder.getNcbiGi().toArray(new String[0]), taxId);
				bestGuess = (mapped != null) ? new CrossReferenceImpl("uniprotkb", mapped) : new CrossReferenceImpl("ncbiGI", rFinder.getNcbiGi().first());
			}else if(rFinder.getEnsembl().size() > 0){
				mapped = map(rFinder.getEnsembl().toArray(new String[0]), taxId);
				bestGuess = (mapped != null) ? new CrossReferenceImpl("uniprotkb", mapped) : new CrossReferenceImpl("ensembl", rFinder.getEnsembl().first());
			}
		}
	
		return bestGuess;
	}
	
	private String[] getIdsStringArray(List<CrossReference> ids){
		List<String> strLst = new ArrayList<String>();
		
		Iterator<CrossReference> i = ids.iterator();
		while(i.hasNext()){
			strLst.add(i.next().getIdentifier());
		}
		return strLst.toArray(new String[0]);
	}
	
	private String map(String[] ids, String taxId) throws CorruptIndexException, IOException, ParseException {
		String mapped = null;
		TreeSet<String> accessions = uniprot.smartSearch(ids, taxId);
		if(accessions.size() == 1){
			mapped = accessions.first();
		}
		return mapped;
	}

	public void setServicesToSkip(List<String> servicesToSkip) {
		this.servicesToSkip = servicesToSkip;
	}
}


