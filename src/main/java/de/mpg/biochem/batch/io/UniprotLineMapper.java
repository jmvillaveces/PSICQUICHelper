package de.mpg.biochem.batch.io;

import org.apache.log4j.Logger;
import org.springframework.batch.item.file.LineMapper;

public class UniprotLineMapper implements LineMapper<String[]>{
	
	private Logger logger = Logger.getLogger(UniprotLineMapper.class);

	public UniprotLineMapper(){}

	@Override
	public String[] mapLine(String line, int lineNumber) throws Exception {
		try{
			return line.split("\t");
		}catch(Exception e){
			logger.warn("Cannot parse line ["+lineNumber+"] "+line);
		}
		return new String[0];
	}
}
