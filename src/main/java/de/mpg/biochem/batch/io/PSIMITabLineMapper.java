package de.mpg.biochem.batch.io;

import org.apache.log4j.Logger;
import org.springframework.batch.item.file.LineMapper;

import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.model.BinaryInteraction;

public class PSIMITabLineMapper implements LineMapper<BinaryInteraction>{

	private PsimiTabReader reader;
	private Logger logger = Logger.getLogger(PSIMITabLineMapper.class);
	
	public PSIMITabLineMapper(){
		reader = new PsimiTabReader();
	}
	
	@Override
	public BinaryInteraction mapLine(String line, int lineNumber) throws Exception {
		try{
			return reader.readLine(line);
		}catch(Exception e){
			logger.warn("Cannot parse line ["+lineNumber+"] "+line+"\n"+e.getMessage());
		}
		return null;
	}
}
