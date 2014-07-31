package de.mpg.biochem.test;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
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
	private JobLauncherTestUtils jobLauncherTestUtils;
	
	@Before
	public void setup() {
		
	}
	
	@Test
    public void testDownloadJob() throws Exception{
		//testing a job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}

	@After
	public void teardown() {
		
	}
	
}
