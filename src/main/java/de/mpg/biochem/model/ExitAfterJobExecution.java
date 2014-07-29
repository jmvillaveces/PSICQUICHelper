package de.mpg.biochem.model;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class ExitAfterJobExecution implements JobExecutionListener {

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
		System.exit(0);
	}

}
