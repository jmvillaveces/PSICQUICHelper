package de.mpg.biochem.batch;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.core.io.FileSystemResource;

import psidev.psi.mi.tab.model.BinaryInteraction;

public class BinaryInteractionIoFactory implements FlatFileItemIoFactory<BinaryInteraction> {

	@Override
	public FlatFileItemReader<BinaryInteraction> getReader(FileSystemResource r) {
		FlatFileItemReader<BinaryInteraction> reader = new FlatFileItemReader<BinaryInteraction>();
		reader.setResource(r);
		reader.setLineMapper(new PSIMITabLineMapper());
		return reader;	
	}

	@Override
	public FlatFileItemWriter<BinaryInteraction> getWriter(FileSystemResource r) {
		FlatFileItemWriter<BinaryInteraction> writer = new FlatFileItemWriter<BinaryInteraction>();
		writer.setResource(r);
		writer.setLineAggregator(new PSIMITabLineAgregator());
		return writer;
	}

}
