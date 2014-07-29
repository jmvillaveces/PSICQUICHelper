package de.mpg.biochem.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hupo.psi.mi.psicquic.registry.ServiceType;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import de.mpg.biochem.model.Service;
import de.mpg.biochem.model.ServiceHandler;

public class PSICQUICServicePartitioner implements Partitioner {

	private ServiceHandler serviceHandler;
	private PsicquicRegistryClient registryClient;
	private List<String> services;
	private String psicquicQuery;
	
	private static final Logger logger = Logger.getLogger(PSICQUICServicePartitioner.class);
	
	public PSICQUICServicePartitioner() {
		psicquicQuery = "*";
		services = new ArrayList<String>();
	}
	
	@Override
	public Map<String, ExecutionContext> partition(int threads) {
		
		if(serviceHandler == null) { 
			throw new IllegalArgumentException("serviceHandler is not defined");
		}
		
		if(registryClient == null) {
			throw new IllegalArgumentException("registryClient is not defined");
		}
		
		Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>();
		
		toLowerCase(services);
		try {
			List<ServiceType> psicquicServices = registryClient.listServices();
			
			for (ServiceType psicquicService : psicquicServices) {
				
				if(services.size() == 0 || services.contains(psicquicService.getName().toLowerCase())){
					
					if(!psicquicService.isActive()) {
						logger.info("Service "+psicquicService.getName()+" is offline.");
					}else {
						Service service = serviceHandler.getService(psicquicService.getName());
						if(service == null){
							service = new Service(psicquicService.getName(), psicquicService.getRestUrl(), psicquicService.getVersion(), psicquicService.getCount(), psicquicQuery);
							service.setQuery(psicquicQuery);
							serviceHandler.add(service);
							
							ExecutionContext ctx = new ExecutionContext();
							ctx.put("service", service);
							ctx.put("name", service.getName());
							result.put(service.getName(), ctx);
							
						}else if(!service.getVersion().equals(psicquicService.getVersion()) || service.getCount() != psicquicService.getCount() || !psicquicQuery.equals(service.getQuery())){
							service.setCount(psicquicService.getCount());
							service.setVersion(psicquicService.getVersion());
							service.setQuery(psicquicQuery);
							
							serviceHandler.add(service);
							
							ExecutionContext ctx = new ExecutionContext();
							ctx.put("service", service);
							ctx.put("name", service.getName());
							result.put(service.getName(), ctx);
						}else if(!service.isIndexed()){
							
							ExecutionContext ctx = new ExecutionContext();
							ctx.put("service", service);
							ctx.put("name", service.getName());
							result.put(service.getName(), ctx);
							
						}
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
		
	}

	public void setServiceHandler(ServiceHandler serviceHandler) {
		this.serviceHandler = serviceHandler;
	}

	public void setRegistryClient(PsicquicRegistryClient registryClient) {
		this.registryClient = registryClient;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

	public void setPsicquicQuery(String psicquicQuery) {
		this.psicquicQuery = psicquicQuery;
	}
	
	private void toLowerCase(List<String> lst) {
		ListIterator<String> iterator = lst.listIterator();
	    while (iterator.hasNext()){
	        iterator.set(iterator.next().toLowerCase());
	    }
	}
}
