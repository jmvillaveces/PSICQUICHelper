package de.mpg.biochem.model;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.mpg.biochem.util.XMLConverter;

public class ServiceHandler {
	
	private XMLConverter XMLConverter; 
	private ServiceList sList;
	private String path;
	
	public ServiceHandler() {
		sList = new ServiceList();
	}
	
	public void setXMLConverter(XMLConverter XMLConverter) {
		this.XMLConverter = XMLConverter;
	}

	public void setPath(String path) throws IOException {
		this.path = path;
		load();
	}
	
	public  void  load() throws IOException{
		File f = new File(path);
		if(f.exists())
			sList = (ServiceList) XMLConverter.convertFromXMLToObject(path);
	}
	
	private synchronized  void  save() throws IOException{
		XMLConverter.convertFromObjectToXML(sList, path);
	}
	
	public synchronized void add(Service service) throws IOException{
		sList.add(service);
	}
	
	public List<Service> getServiceList(){
		return sList.getServices();
	}
	
	public Service getService(String serviceName){
		return sList.getService(serviceName);
	}
}


