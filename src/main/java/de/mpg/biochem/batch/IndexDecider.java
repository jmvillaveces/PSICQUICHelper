package de.mpg.biochem.batch;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class IndexDecider implements JobExecutionDecider {
	
	private String path;
	
	private Logger logger = Logger.getLogger(IndexDecider.class);
	
	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		
		String uniprotDownloadPath = path + "/idmapping_selected.tab.gz",
				ncbiDownloadPath = path + "/gene2accession.gz";
		
		File mapping = new File(uniprotDownloadPath);
		if(mapping.exists() && FileUtils.isFileNewer(mapping, new DateTime().minusWeeks(2).toDate())){
			logger.info("Decided to create index");
			return new FlowExecutionStatus("CONTINUE");
		}
		
		mapping = new File(ncbiDownloadPath);
		if(mapping.exists() && FileUtils.isFileNewer(mapping, new DateTime().minusWeeks(2).toDate())){
			logger.info("Decided to create index");
			return new FlowExecutionStatus("CONTINUE");
		}
		
		logger.info("Decided skip index creation");
		return new FlowExecutionStatus("SKIP");
	}

	public void setPath(String path) {
		this.path = path;
	}
}
