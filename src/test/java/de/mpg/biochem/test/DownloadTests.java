package de.mpg.biochem.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.AssertFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.biochem.model.ServiceHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:download-test-context.xml" })
public class DownloadTests {
	
	@Autowired
    private ServiceHandler serviceHandler;
	
	@Autowired
	private Job job;
	
	@Autowired
	private JobLauncher jobLauncher;
	
	private final String PATH = "";
	private File destination;
	
	@Before
	public void setup() throws IOException {
		URL url = new URL("http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/query/idA:P37840%20AND%20idB:P37840");
		destination = new File("destination.tab");
		
		FileUtils.copyURLToFile(url, destination);
	}
	
	@Test
    public void testDownloadJob() throws Exception{
		serviceHandler.setPath(PATH+"services.xml");
		
		JobParametersBuilder jobParameters = new JobParametersBuilder();
		jobParameters.addString("path", PATH);
		jobParameters.addString("services", "intact");
		jobParameters.addString("query", "idA:P37840 AND idB:P37840");
		
		JobExecution execution = jobLauncher.run(job, jobParameters.toJobParameters());
        
		while(!BatchStatus.COMPLETED.equals(execution.getExitStatus())) {
			Thread.sleep(1000);
		}
		
		AssertFile.assertFileEquals(destination, new File(PATH+"IntAct"));
	}

	@After
	public void teardown() {
		destination.delete();
		new File("services.xml").delete();
		new File("IntAct.tab").delete();
	}
	
}
