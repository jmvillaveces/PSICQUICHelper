package de.mpg.biochem.batch.tasklet;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hupo.psi.mi.psicquic.registry.ServiceType;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.task.TaskExecutor;

import de.mpg.biochem.model.Service;
import de.mpg.biochem.model.ServiceHandler;

public class FetchInteractionsTasklet  implements Tasklet{

	private PsicquicRegistryClient registryClient;
	private TaskExecutor taskExecutor;
	private ServiceHandler serviceHandler;
	
	private String outPath;
	private String psicquicQuery;
	private int maxResults;
	private int maxAtempts;
	private int waiting;
	private List<String> services;
	
	private static final Logger logger = Logger.getLogger(FetchInteractionsTasklet.class);
	
	public FetchInteractionsTasklet(PsicquicRegistryClient registryClient, TaskExecutor taskExecutor, ServiceHandler serviceHandler){
		this.registryClient = registryClient;
		this.taskExecutor = taskExecutor;
		this.serviceHandler = serviceHandler;
		
		psicquicQuery = "*";
		maxResults = 15000;
		maxAtempts = 3;
		waiting = 300000; // 5 minutes
		services = new ArrayList<String>();
	}
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		if(outPath == null) { 
			throw new IllegalArgumentException("outPath is not defined");
		}
		
		toLowerCase(services);
		
		List<Service> toIndex = new ArrayList<Service>();
		List<ServiceType> psicquicServices = registryClient.listServices();
		
		for (ServiceType psicquicService : psicquicServices) {
			
			if(services.size() == 0 || services.contains(psicquicService.getName().toLowerCase())){
				
				if(!psicquicService.isActive()) {
					logger.info("Service "+psicquicService.getName()+" is offline.");
				}else {
					Service service = serviceHandler.getService(psicquicService.getName());
					if(service == null){
						service = new Service(psicquicService.getName(), psicquicService.getRestUrl(), psicquicService.getVersion(), psicquicService.getCount(), psicquicQuery);
						serviceHandler.add(service);
						toIndex.add(service);
					}else if(!service.isIndexed()){
						toIndex.add(service);
					}else if(!service.getVersion().equals(psicquicService.getVersion()) || service.getCount() != psicquicService.getCount() || !psicquicQuery.equals(service.getQuery())){
						service.setCount(psicquicService.getCount());
						service.setVersion(psicquicService.getVersion());
						
						serviceHandler.add(service);
						toIndex.add(service);
					}
				}
			}
		}
		
		CountDownLatch latch = new CountDownLatch(toIndex.size());
		for(Service service : toIndex){
			
			PsicquicServerQueryTask task = new PsicquicServerQueryTask(service, serviceHandler, latch);
			task.setPath(outPath);
			task.setMaxResults(maxResults);
			task.setMaxAtempts(maxAtempts);
			task.setWaiting(waiting);
			taskExecutor.execute(task);
		}
		latch.await();
		
		Thread.sleep(2000);
		return RepeatStatus.FINISHED;
	}
	
	private void toLowerCase(List<String> strings){
	    ListIterator<String> iterator = strings.listIterator();
	    while (iterator.hasNext()){
	        iterator.set(iterator.next().toLowerCase());
	    }
	}

	public void setRegistryClient(PsicquicRegistryClient registryClient) {
		this.registryClient = registryClient;
	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setServiceHandler(ServiceHandler serviceHandler) {
		this.serviceHandler = serviceHandler;
	}

	public void setOutPath(String outPath) {
		this.outPath = outPath;
	}

	public void setPsicquicQuery(String psicquicQuery) {
		this.psicquicQuery = psicquicQuery;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public void setMaxAtempts(int maxAtempts) {
		this.maxAtempts = maxAtempts;
	}

	public void setWaiting(int waiting) {
		this.waiting = waiting;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}
}
