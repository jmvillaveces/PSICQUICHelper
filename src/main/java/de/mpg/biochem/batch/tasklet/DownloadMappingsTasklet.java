package de.mpg.biochem.batch.tasklet;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class DownloadMappingsTasklet implements Tasklet  {

	private Logger logger = Logger.getLogger(DownloadMappingsTasklet.class);
	
	private String uniprotUrl;
	private String ncbiUrl;
	
	private String path;
	
	
	public DownloadMappingsTasklet(){}
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		String uniprotDownloadPath = path + "idmapping_selected.tab.gz",
				ncbiDownloadPath = path + "gene2accession.gz";
			
		File mapping = new File(uniprotDownloadPath);
		if(mapping.exists() && FileUtils.isFileNewer(mapping, new DateTime().minusWeeks(2).toDate())){
			logger.info("Uniprot mapping file is up to date in "+uniprotDownloadPath);
			//Do nothing
		}else{
			//Download the file
			logger.info("Downloading uniprot mapping file to "+uniprotDownloadPath );
			FileUtils.copyURLToFile(new URL(uniprotUrl), mapping);
		}
			
		mapping = new File(ncbiDownloadPath);
		if(mapping.exists() && FileUtils.isFileNewer(mapping, new DateTime().minusWeeks(2).toDate())){
			logger.info("NCBI mapping file is up to date in "+ncbiDownloadPath);
			//Do nothing
		}else{
			//Download the file
			logger.info("Downloading ncbi mapping file to "+ncbiDownloadPath);
			FileUtils.copyURLToFile(new URL(ncbiUrl), mapping);
		}
		
		return RepeatStatus.FINISHED;
	}

	public void setUniprotUrl(String uniprotUrl) {
		this.uniprotUrl = uniprotUrl;
	}

	public void setNcbiUrl(String ncbiUrl) {
		this.ncbiUrl = ncbiUrl;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
