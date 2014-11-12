package de.mpg.biochem.model;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;

public class ExitAfterJobExecution implements JobExecutionListener {

	@Autowired
	private AbstractApplicationContext ctx;
	
	@Override
	public void beforeJob(JobExecution jobExecution) {
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		//Kill Execution
		if (!jobExecution.getAllFailureExceptions().isEmpty()) {
			ExitStatus exitStatus = ExitStatus.FAILED;
			for (Throwable e : jobExecution.getAllFailureExceptions()) {
				exitStatus = exitStatus.addExitDescription(e);
			}
			jobExecution.setExitStatus(exitStatus);
		}
		jobExecution.setExitStatus(ExitStatus.COMPLETED);
		
		ctx.close();
		
		System.exit(0);
	}

}
