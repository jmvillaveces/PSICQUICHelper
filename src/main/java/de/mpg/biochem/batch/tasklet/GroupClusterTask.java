package de.mpg.biochem.batch.tasklet;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.FileSystemResource;

import psidev.psi.mi.tab.model.BinaryInteraction;
import uk.ac.ebi.enfin.mi.cluster.Encore2Binary;
import uk.ac.ebi.enfin.mi.cluster.EncoreInteraction;
import uk.ac.ebi.enfin.mi.cluster.score.InteractionClusterScore;
import uk.ac.ebi.enfin.mi.score.scores.MIScore;
import de.mpg.biochem.batch.BinaryInteractionComparator;
import de.mpg.biochem.batch.io.FlatFileItemIoFactory;



public class GroupClusterTask implements Tasklet {

	
    private static final Log logger = LogFactory.getLog(GroupClusterTask.class);
	
    private FileSystemResource inputResource;
    private FileSystemResource outputResource;
    private FlatFileItemIoFactory<BinaryInteraction> inputIoFactory;
    private FlatFileItemIoFactory<BinaryInteraction> outputIoFactory;
    
    private FlatFileItemWriter<BinaryInteraction> writer;
    private FlatFileItemReader<BinaryInteraction> reader;

    // Set priority for molecule accession mapping (find more database names in the MI Ontology, MI:0473)
    private String allMappingNames = "uniprotkb,intact,ddbj/embl/genbank,chebi,irefindex,hgnc,ensembl";
    
    // Score name
    private String scoreName = "miscore";
    
	public GroupClusterTask(){
		inputResource = null;
		outputResource = null;
		inputIoFactory = null;
		outputIoFactory = null;
	}
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		// Check tasklet's state
		if (inputResource == null)
			throw new IllegalStateException("no input resource specified");
		if (outputResource == null)
			throw new IllegalStateException("no output resource specified");
		if (inputIoFactory == null)
			throw new IllegalStateException("no input I/O factory defined");
		if (outputIoFactory == null)
			throw new IllegalStateException("no output I/O factory defined");
		
		
		// Get output and input files
		File inputFile = inputResource.getFile();

		// Execution context
		ExecutionContext context = new ExecutionContext();

		// Prepare reader and writer
		reader = inputIoFactory.getReader(inputResource);
		reader.open(context);
		
		logger.info("input file, '" + inputFile.getAbsolutePath()+ "' opened for reading");

		
		writer = inputIoFactory.getWriter(outputResource);
		writer.setTransactional(false);
		writer.open(context);
		
		BinaryInteractionComparator comparator = new BinaryInteractionComparator();
		
		List<BinaryInteraction> toCluster = new ArrayList<BinaryInteraction>();
		BinaryInteraction interaction = reader.read();
		BinaryInteraction previous = null;
		while(interaction != null){
			
			if(previous != null && comparator.compare(previous, interaction) == 0){
				toCluster.add(interaction);
			}else{
				previous = interaction;
				
				if(toCluster.size()>=10000){
					clusterAndScore(toCluster);
					toCluster.clear();
				}
				toCluster.add(interaction);
			}
			interaction = reader.read();
		}
		
		if(toCluster.size()>0)
			clusterAndScore(toCluster);
		
		reader.close();
		writer.close();
		return  RepeatStatus.FINISHED;
	}
	
	private void clusterAndScore(List<BinaryInteraction> interactions){
		try {
			// Cluster data
			MIScore miscore = new MIScore(false);
			InteractionClusterScore iCluster = new InteractionClusterScore(miscore);
			iCluster.setBinaryInteractionIterator((Iterator<BinaryInteraction>) interactions.iterator());
			iCluster.setMappingIdDbNames(allMappingNames);
			iCluster.setScoreName(scoreName);
			iCluster.runService();
			
			write(iCluster.getInteractionMapping().values(), new Encore2Binary(iCluster.getMappingIdDbNames()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setInputResource(FileSystemResource inputResource) {
		this.inputResource = inputResource;
	}

	public void setOutputResource(FileSystemResource outputResource) {
		this.outputResource = outputResource;
	}

	public void setInputIoFactory(FlatFileItemIoFactory<BinaryInteraction> inputIoFactory) {
		this.inputIoFactory = inputIoFactory;
	}

	public void setOutputIoFactory(FlatFileItemIoFactory<BinaryInteraction> outputIoFactory) {
		this.outputIoFactory = outputIoFactory;
	}
	
	public void setAllMappingNames(String allMappingNames) {
		this.allMappingNames = allMappingNames;
	}

	public void setScoreName(String scoreName) {
		this.scoreName = scoreName;
	}

	private synchronized void write(Collection<EncoreInteraction> interactions,  Encore2Binary iConverter) throws Exception{
		List<BinaryInteraction> biInteractions = new ArrayList<BinaryInteraction>();
		for(EncoreInteraction eI : interactions){
			biInteractions.add(iConverter.getBinaryInteractionForScoring(eI));
	    }
		writer.write(biInteractions);
	}
}