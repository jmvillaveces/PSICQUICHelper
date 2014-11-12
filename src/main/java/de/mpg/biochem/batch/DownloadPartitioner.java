package de.mpg.biochem.batch;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

public class DownloadPartitioner implements Partitioner{

	private Logger logger = Logger.getLogger(DownloadPartitioner.class);
	private String uniprotUrl;
	private String tarUrl;
	private String path;
	
	@Override
	public Map<String, ExecutionContext> partition(int threads) {
		
		String uniprotDownloadPath = path + uniprotUrl.substring(uniprotUrl.lastIndexOf('/', uniprotUrl.length())).replace("/", ""),
				tarDownloadPath = path + tarUrl.substring(tarUrl.lastIndexOf('/', tarUrl.length())).replace("/", "");
		
		Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>();
		
		File mapping = new File(uniprotDownloadPath);
		if(mapping.exists() && FileUtils.isFileNewer(mapping, new DateTime().minusWeeks(4).toDate())){
			logger.info("Uniprot mapping file is up to date in "+uniprotDownloadPath);
			//Do nothing
		}else{
			//Download the file
			logger.info("Downloading uniprot mapping file to "+uniprotDownloadPath );
			
			ExecutionContext ctx = new ExecutionContext();
			ctx.put("url", uniprotUrl);
			ctx.put("path", uniprotDownloadPath);
			result.put(uniprotUrl, ctx);
		}
			
		mapping = new File(tarDownloadPath);
		if(mapping.exists() && FileUtils.isFileNewer(mapping, new DateTime().minusWeeks(4).toDate())){
			logger.info("TAR mapping file is up to date in "+tarDownloadPath);
			//Do nothing
		}else{
			//Download the file
			logger.info("Downloading TAR mapping file to "+tarDownloadPath);
			
			ExecutionContext ctx = new ExecutionContext();
			ctx.put("url", tarUrl);
			ctx.put("path", tarDownloadPath);
			result.put(tarUrl, ctx);
		}
		
		return result;
	}
	
	public void setUniprotUrl(String uniprotUrl) {
		this.uniprotUrl = uniprotUrl;
	}

	public void setTarUrl(String tarUrl) {
		this.tarUrl = tarUrl;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
