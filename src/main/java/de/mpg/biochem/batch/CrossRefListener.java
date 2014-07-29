package de.mpg.biochem.batch;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.CrossReference;
import psidev.psi.mi.tab.model.CrossReferenceImpl;

public class CrossRefListener implements ItemReadListener<BinaryInteraction>, StepExecutionListener {
	
	private StepExecution stepExecution;

	public CrossRefListener(){}
	
	@Override
	public void afterRead(BinaryInteraction interaction) {
		if(interaction != null){
			String source = stepExecution.getExecutionContext().get("source").toString();
			
			CrossReference cr = new CrossReferenceImpl();
			cr.setDatabase("iAtlas");
			cr.setIdentifier(source);
			interaction.getSourceDatabases().add(cr);
		}
	}

	@Override
	public void beforeRead() {}

	@Override
	public void onReadError(Exception ex) {}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return null;
	}
}
