package de.mpg.biochem.batch;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.MultiResourceItemReader;

public class WrappedMrirToGetCurrentResource<T> extends MultiResourceItemReader<T> {
 
	private String source = "";
	private ExecutionContext executionContext;
 
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
    	this.executionContext = executionContext;
        super.open(executionContext);
        // current.resource is only available here if it's a restart
        setSource();
    }
 
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
    	this.executionContext = executionContext;
    	super.update(executionContext);
        // one of the most reliable positions to get the current.resource
    	setSource();
    }
 
    @Override
    public void close() throws ItemStreamException {
        super.close();
    }
 
    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        T result = super.read();
        // another position to get the actual current.resource, has to be called
        // after the first read
        setSource();
        return result;
    }
    
    private void setSource(){
    	//super.
    	if (super.getCurrentResource() != null && super.getCurrentResource().getFilename() != null) {
            if(!source.equals(super.getCurrentResource().getFilename())){
            	source = super.getCurrentResource().getFilename().substring(0, super.getCurrentResource().getFilename().indexOf(".tab"));
            	executionContext.put("source", source);
            }
        }
    }
}