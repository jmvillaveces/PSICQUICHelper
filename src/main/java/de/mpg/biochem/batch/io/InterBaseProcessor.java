package de.mpg.biochem.batch.io;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.batch.item.ItemProcessor;

import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.BinaryInteractionImpl;
import psidev.psi.mi.tab.model.CrossReference;
import psidev.psi.mi.tab.model.CrossReferenceImpl;
import psidev.psi.mi.tab.model.Interactor;
import de.mpg.biochem.service.UniProtIndex;

public class InterBaseProcessor implements ItemProcessor<BinaryInteraction, BinaryInteraction[]> {

	
	private String[] DBS = new String[] { "genbank indentifier", "entrezgene/locuslink", "entrez gene/locuslink", "refseq", "ddbj/embl/genbank", "tair"};
	private List<String> servicesToSkip;
	
	private UniProtIndex uniprot;
	private File notMapped;
	
	public InterBaseProcessor(String indexPath, String filePath) throws IOException{
		uniprot = new UniProtIndex(indexPath);
		
		notMapped = new File(filePath);
		if(notMapped.exists())
			notMapped.delete();
	}

	@Override
	public BinaryInteraction[] process(BinaryInteraction interaction) throws Exception {
		
		CrossReference cr = (CrossReference) interaction.getSourceDatabases().get(interaction.getSourceDatabases().size() -1);
		
		//Check if interaction should be analyzed
		if(!servicesToSkip.contains(cr.getIdentifier())){
			List<CrossReference> idsA = processInteractor(interaction.getInteractorA());
			List<CrossReference> idsB = processInteractor(interaction.getInteractorB());
			
			if(idsA.size() > 0 && idsB.size() > 0) {
				BinaryInteraction[] ints = getInteractions(idsA, idsB, interaction);
				return ints;
			}
		}
		
		List<BinaryInteraction> bis = new ArrayList<BinaryInteraction>();
		bis.add(interaction);
		return bis.toArray(new BinaryInteraction[0]);
	}
	
	private BinaryInteraction[] getInteractions(List<CrossReference> idsA, List<CrossReference> idsB, BinaryInteraction interaction) {
		
		List<BinaryInteraction> bis = new ArrayList<BinaryInteraction>();
		for(CrossReference idA : idsA) {
			
			List<CrossReference> intAIds = new ArrayList<CrossReference>();
			intAIds.add(idA);
			
			Interactor intA = createInteractor(intAIds, interaction.getInteractorA());
			for(CrossReference idB : idsB) {
				
				List<CrossReference> intBIds = new ArrayList<CrossReference>();
				intBIds.add(idB);
				
				Interactor intB = createInteractor(intBIds, interaction.getInteractorB());
				
				BinaryInteraction bi = new BinaryInteractionImpl(intA, intB);
				
				bi.setAnnotations(interaction.getAnnotations());
				bi.setAuthors(interaction.getAuthors());
				bi.setChecksums(interaction.getChecksums());
				bi.setComplexExpansion(bi.getComplexExpansion());
				bi.setConfidenceValues(interaction.getConfidenceValues());
				bi.setCreationDate(interaction.getCreationDate());
				bi.setDetectionMethods(interaction.getDetectionMethods());
				bi.setHostOrganism(interaction.getHostOrganism());
				bi.setInteractionAcs(interaction.getInteractionAcs());
				bi.setInteractionTypes(interaction.getInteractionTypes());
				bi.setNegativeInteraction(interaction.isNegativeInteraction());
				bi.setParameters(interaction.getParameters());
				bi.setPublications(interaction.getPublications());
				bi.setSourceDatabases(interaction.getSourceDatabases());
				bi.setUpdateDate(interaction.getUpdateDate());
				bi.setXrefs(interaction.getXrefs());
				
				bis.add(bi);
			}
		}
		return bis.toArray(new BinaryInteraction[0]);
	}
	
	private Interactor createInteractor(List<CrossReference> ids, Interactor interactor) {
		Interactor newInteractor = new Interactor();
		newInteractor.setIdentifiers(ids);
		newInteractor.setAliases(interactor.getAliases());
		
		List<CrossReference> altIds = new ArrayList<CrossReference>(interactor.getIdentifiers());
		altIds.addAll(interactor.getAlternativeIdentifiers());
		
		newInteractor.setAlternativeIdentifiers(altIds);
		newInteractor.setAnnotations(interactor.getAnnotations());
		newInteractor.setBiologicalRoles(interactor.getBiologicalRoles());
		newInteractor.setChecksums(interactor.getChecksums());
		newInteractor.setFeatures(interactor.getFeatures());
		newInteractor.setInteractorTypes(interactor.getInteractorTypes());
		newInteractor.setOrganism(interactor.getOrganism());
		newInteractor.setParticipantIdentificationMethods(interactor.getParticipantIdentificationMethods());
		newInteractor.setStoichiometry(interactor.getStoichiometry());
		newInteractor.setXrefs(interactor.getXrefs());
		
		return newInteractor;
	}
	
	private List<CrossReference> processInteractor(Interactor interactor) throws IOException, ParseException {
		
		//In case is a molecule interacts with itself
		if(interactor != null && interactor.getIdentifiers().size() > 0){
			List<CrossReference> uniprotkb = getElementsByDB(interactor.getIdentifiers(), "uniprotkb");
			
			if(uniprotkb.size() == 0) {
				uniprotkb = getElementsByDB(interactor.getAlternativeIdentifiers(), "uniprotkb");
				if(uniprotkb.size() != 1) {
					return getMappings(interactor);
				}else {
					return uniprotkb;
				}
			}else {
				return uniprotkb;
			}
		}
		return new ArrayList<CrossReference>();
	}
	
	private List<CrossReference> getElementsByDB(List<CrossReference> ids, String db) {
		
		List<CrossReference> newIds = new ArrayList<CrossReference>();
		for(CrossReference id : ids) {
			if(id.getDatabase().equalsIgnoreCase(db)) newIds.add(id);
		}
		return newIds;
	}
	
	private List<CrossReference> getMappings(Interactor interactor) throws IOException, ParseException {
		
		//String taxId = interactor.getOrganism().getTaxid();
		
		TreeSet<String> mappings = new TreeSet<String>();
		List<CrossReference> ids = interactor.getIdentifiers();
		for(String db : DBS) {
			List<CrossReference> tmp = getElementsByDB(ids, db);
			if(tmp.size() > 0) {
				mappings = uniprot.search(tmp.get(0).getIdentifier());
				if(mappings.size() > 0) { 
					break;
				}
			}
		}
		
		ids = new ArrayList<CrossReference>();
		for(String id : mappings) {
			ids.add(new CrossReferenceImpl("uniprotkb", id));
		}
		return ids;
	}
	
	public void setServicesToSkip(List<String> servicesToSkip) {
		this.servicesToSkip = servicesToSkip;
	}
	
	/*public void processInteractor(Interactor interactor) throws MalformedURLException, SAXException, IOException, ParseException, InterruptedException {
		
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
	}*/
}


