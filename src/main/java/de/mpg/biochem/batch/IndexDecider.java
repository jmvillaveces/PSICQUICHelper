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
	
	private String uniprotUrl;
	private String tarUrl;
	private String path;
	
	private Logger logger = Logger.getLogger(IndexDecider.class);
	
	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		
		path = jobExecution.getJobParameters().getString("mappingPath");
		uniprotUrl = jobExecution.getJobParameters().getString("uniprotUrl");
		tarUrl = jobExecution.getJobParameters().getString("tarUrl");
		
		
		String uniprotDownloadPath = path + uniprotUrl.substring(uniprotUrl.lastIndexOf('/', uniprotUrl.length())),
				tarDownloadPath = path + tarUrl.substring(tarUrl.lastIndexOf('/', tarUrl.length()));
		
		File index = new File(path+"index");
		if(!index.exists()) {
			logger.info("Decided to create index");
			return new FlowExecutionStatus("CONTINUE");
		}
		
		if(FileUtils.isFileNewer(index, new DateTime().minusWeeks(2).toDate())) {
			logger.info("Decided skip index creation");
			return new FlowExecutionStatus("SKIP");
		}
		
		File mapping = new File(uniprotDownloadPath);
		if(mapping.exists() && FileUtils.isFileNewer(mapping, new DateTime().minusWeeks(2).toDate())){
			logger.info("Decided to create index");
			return new FlowExecutionStatus("CONTINUE");
		}
		
		mapping = new File(tarDownloadPath);
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

	public void setUniprotUrl(String uniprotUrl) {
		this.uniprotUrl = uniprotUrl;
	}

	public void setTarUrl(String tarUrl) {
		this.tarUrl = tarUrl;
	}
}
