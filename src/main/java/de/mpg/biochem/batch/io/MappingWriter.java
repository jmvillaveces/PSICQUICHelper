package de.mpg.biochem.batch.io;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;

import psidev.psi.mi.tab.model.BinaryInteraction;

@SuppressWarnings("rawtypes")
public class MappingWriter implements ItemWriter<BinaryInteraction[]>, StepExecutionListener {

	private FlatFileItemWriter<BinaryInteraction> writer;
	
	public MappingWriter(){}
	
	@Override
	public void write(List<? extends BinaryInteraction[]> lst) throws Exception {
		
		List<BinaryInteraction> ints = new ArrayList<BinaryInteraction>();
		for(BinaryInteraction[] arr : lst) {
			
			for(BinaryInteraction bi : arr) {
				if (ints.size() < 30000) {
					ints.add(bi);
				}else {
					writer.write(ints);
					ints = new ArrayList<BinaryInteraction>();
				}
			}
		}
		
		if(ints.size() > 0) writer.write(ints);
	}

	public void setWriter(FlatFileItemWriter<BinaryInteraction> writer) {
		this.writer = writer;
	}

	@Override
	public ExitStatus afterStep(StepExecution arg0) {
		writer.close();
		return null;
	}

	@Override
	public void beforeStep(StepExecution arg0) {
		this.writer.open(arg0.getExecutionContext());
	}	
}
