package de.mpg.biochem.batch.tasklet;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class DownloadFileTasklet implements Tasklet{

	private String url;
	private String path;
	
	public DownloadFileTasklet() {}
	
	public DownloadFileTasklet(String url, String path) {
		this.url = url;
		this.path = path;
	}
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		File file = new File(path);
		FileUtils.copyURLToFile(new URL(url), file);
		
		return RepeatStatus.FINISHED;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	
}
