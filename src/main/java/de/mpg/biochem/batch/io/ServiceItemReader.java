package de.mpg.biochem.batch.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.hupo.psi.mi.psicquic.wsclient.PsicquicSimpleClient;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import de.mpg.biochem.batch.tasklet.FetchInteractionsTasklet;
import de.mpg.biochem.model.Service;

public class ServiceItemReader implements ItemReader<String> {

	private Service service;
	private PsicquicSimpleClient client;
	private long count;
	private long range;
	private int from;
	private InputStream inputStream = null;
	private BufferedReader br = null;
	
	private static final Logger logger = Logger.getLogger(ServiceItemReader.class);
	
	public ServiceItemReader(Service service) {
		this.service = service;
		this.client = new PsicquicSimpleClient(service.getRestURL());
		
		if(service.getQuery().equals("*")) {
			count = service.getCount();
		}else {
			try {
				count = getQueryCount();
			} catch (IOException e) {
				count = 0;
			}
		}
		range = (count < 15000) ? count : 15000;
		from = 0;
		
		logger.info("Ready to download data from "+service.getName()+" with query = "+service.getQuery()+" and query count = "+count);
	}
	
	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		
		if(br == null) getData();
			
		String line = br.readLine();
		if(line == null) {
			getData();
			line = br.readLine();
			
			if (line == null) {
				logger.info("Got data from "+service.getName());
			}
		}
		return line;
		
	}
	
	private void getData() throws IOException {
		close();
		
		logger.debug(service.getName()+" - Reading "+from+" out of "+count);
		inputStream  = client.getByQuery(service.getQuery(), PsicquicSimpleClient.MITAB25, from, (int) range);
		from += range;
		br = new BufferedReader(new InputStreamReader(inputStream));
	}
	
	private void close() {
		try {
			inputStream.close();
			br.close();
		}catch(Exception e) {
			//logger.warn("Couldn't close stream");
		}
	}
	
	private long getQueryCount() throws IOException {
		return client.countByQuery(service.getQuery());
	}

	public void setService(Service service) {
		this.service = service;
	}

	public void setClient(PsicquicSimpleClient client) {
		this.client = client;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public void setFrom(int from) {
		this.from = from;
	}
}
