package de.mpg.biochem.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServiceList {
	
	private List<Service> services;
	
	public ServiceList() {
		this.services = new ArrayList<Service>();
	}
	
	public List<Service> getServices() {
		return services;
	}
	
	public void add(Service service) throws IOException{
		for(Service serv : services){
			if(serv.getName().equalsIgnoreCase(service.getName())){
				services.remove(serv);
				break;
			}
		}
		services.add(service);
	}
	
	public Service getService(String serviceName){
		for(Service serv : services){
			if(serv.getName().equalsIgnoreCase(serviceName)){
				return serv;
			}
		}
		return null;
	}
}
