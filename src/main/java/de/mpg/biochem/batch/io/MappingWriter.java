package de.mpg.biochem.batch.io;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;

import psidev.psi.mi.tab.model.BinaryInteraction;

public class MappingWriter implements ItemWriter<BinaryInteraction[]> {

	private FlatFileItemWriter writer;
	
	public MappingWriter(){}
	
	@Override
	public void write(List<? extends BinaryInteraction[]> lst) throws Exception {
		
		writer.open(new ExecutionContext());
		
		List<BinaryInteraction> ints = new ArrayList<BinaryInteraction>();
		for(BinaryInteraction[] arr : lst) {
			for(BinaryInteraction in : arr)
				ints.add(in);
		}
		writer.write(ints);
	}

	public void setWriter(FlatFileItemWriter writer) {
		this.writer = writer;
	}	
}
