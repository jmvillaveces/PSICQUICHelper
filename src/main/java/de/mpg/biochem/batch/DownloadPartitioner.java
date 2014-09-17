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
	private String ncbiUrl;
	private String path;
	
	@Override
	public Map<String, ExecutionContext> partition(int threads) {
		String uniprotDownloadPath = path + "idmapping_selected.tab.gz",
				ncbiDownloadPath = path + "gene2accession.gz";
		
		Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>();
		
		File mapping = new File(uniprotDownloadPath);
		if(mapping.exists() && FileUtils.isFileNewer(mapping, new DateTime().minusWeeks(2).toDate())){
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
			
		mapping = new File(ncbiDownloadPath);
		if(mapping.exists() && FileUtils.isFileNewer(mapping, new DateTime().minusWeeks(2).toDate())){
			logger.info("NCBI mapping file is up to date in "+ncbiDownloadPath);
			//Do nothing
		}else{
			//Download the file
			logger.info("Downloading ncbi mapping file to "+ncbiDownloadPath);
			
			ExecutionContext ctx = new ExecutionContext();
			ctx.put("url", ncbiUrl);
			ctx.put("path", ncbiDownloadPath);
			result.put(ncbiUrl, ctx);
		}
		
		return result;
	}
	
	public void setUniprotUrl(String uniprotUrl) {
		this.uniprotUrl = uniprotUrl;
	}

	public void setNcbiUrl(String ncbiUrl) {
		this.ncbiUrl = ncbiUrl;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
