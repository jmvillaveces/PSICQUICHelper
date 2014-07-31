package de.mpg.biochem.batch.tasklet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.hupo.psi.mi.psicquic.wsclient.PsicquicSimpleClient;

import de.mpg.biochem.model.Service;
import de.mpg.biochem.model.ServiceHandler;
import de.mpg.biochem.util.BiochemFileUtils;

public class PsicquicServerQueryTask implements Runnable {

	private String path;
	private int maxResults;
	private int maxAtempts;
	private int waiting;
	
	private Service service;
	private ServiceHandler serviceHandler;
	private CountDownLatch latch;
	
	private static final Logger logger = Logger.getLogger(PsicquicServerQueryTask.class);
	
	public PsicquicServerQueryTask(Service service, ServiceHandler serviceHandler, CountDownLatch latch){
		this.service = service;
		this.serviceHandler = serviceHandler;
		this.latch = latch;
	}
	
	@Override
	public void run() {
		
		File destination = new File(path+service.getName()+".tab");
		PsicquicSimpleClient client = new PsicquicSimpleClient(service.getRestURL());
		
		logger.info("Fetching data form "+service.getName());
		
		long count = 0;
		int i = 0;
		try {
			count = client.countByQuery(service.getQuery());
		} catch (IOException e) {
			logger.error("Error getting count from "+service.getName(), e);
		}
		
		while(i<count){
			logger.debug(service.getName()+" - Writing "+i+" out of "+count);
			getInteractions(client, i, destination);
			i+=maxResults;
		}
		
		try {
			service.setIndexed(true);
			serviceHandler.add(service);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			latch.countDown();
		}
	}
	
	public void getInteractions(PsicquicSimpleClient client, int from, File destination) {
		try {
			InputStream response = client.getByQuery(service.getQuery(), PsicquicSimpleClient.MITAB25, from, maxResults);
			BiochemFileUtils.copyInputStreamToFile(response, destination, true);
		} catch (IOException e) {
			try {
				Thread.sleep(waiting);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}finally {
				getInteractions(client, from, destination);
			}
		}
	}
	
	
	/*@Override
	public void run() {
		
		File destination = new File(path+service.getName()+".tab");
		PsicquicSimpleClient client = new PsicquicSimpleClient(service.getRestURL());
		
		logger.info("Fetching data form "+service.getName());
		try {
			int i=0, j=1;
			long count = client.countByQuery(service.getQuery());
			boolean errors = false;
			while(i<count){
				
				InputStream response;
				try{
					response = client.getByQuery(service.getQuery(), PsicquicSimpleClient.MITAB25, i, maxResults);
				}catch(Exception e){
					response = null;
				}
				
				if(response != null) {
					logger.debug(service.getName()+" - Writing "+i+" out of "+count);
					try {
						BiochemFileUtils.copyInputStreamToFile(response, destination, true);
					} catch (IOException e) {
						logger.error("Error writing to file "+destination.getPath(), e);
					}finally{
						i+=maxResults;
					}
				}else {
					logger.warn(service.getName()+" - Atempt "+j+" failed getting "+i+" out of "+count);
					if(j>maxAtempts){
						j = 1;
						i += maxResults;
						errors = true;
					}else{
						j++;
						Thread.sleep(waiting);
					}
				}
			}
			
			if(!errors){
				service.setIndexed(true);
				serviceHandler.add(service);
			}
			logger.info("Got data from "+service.getName());
			
		}catch (Exception e) {
			logger.error("error while querying "+service.getName(), e);
		} finally {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally{
				latch.countDown();
			}
		}
	}*/

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public int getMaxAtempts() {
		return maxAtempts;
	}

	public void setMaxAtempts(int maxAtempts) {
		this.maxAtempts = maxAtempts;
	}

	public int getWaiting() {
		return waiting;
	}

	public void setWaiting(int waiting) {
		this.waiting = waiting;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public ServiceHandler getServiceHandler() {
		return serviceHandler;
	}

	public void setServiceHandler(ServiceHandler serviceHandler) {
		this.serviceHandler = serviceHandler;
	}

	public CountDownLatch getLatch() {
		return latch;
	}

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}
}
