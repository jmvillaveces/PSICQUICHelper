package de.mpg.biochem.batch;

import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatListener;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Simple Repeat Listener listening on repeat operations.
 * 
 */
public class SimpleRepeatListener implements RepeatListener {
	
	/**
	 * Called by the framework after each item has been processed, unless the item processing results in an exception.
	 */
	@Override
	public void after(RepeatContext repeatContext, RepeatStatus exitStatus) {
		System.out.println("after iteration " + repeatContext.getStartedCount());
	}

	/**
	 * Called by the framework before each batch item.
	 */
	@Override
	public void before(RepeatContext repeatContext) {
		System.out.println("before iteration " + (repeatContext.getStartedCount() + 1));
	}

	/**
	 * Called once at the end of a complete batch, after normal or abnormal completion.
	 */
	@Override
	public void close(RepeatContext repeatContext) {
		System.out.println("Batch processing ended.");
	}

	/**
	 * Called when a repeat callback fails by throwing an exception.
	 */
	@Override
	public void onError(RepeatContext repeatContext, Throwable error) {
		// TODO Auto-generated method stub
	}

	/**
	 * Called once at the start of a complete batch, before any items are processed.
	 */
	@Override
	public void open(RepeatContext repeatContext) {
		System.out.println("Batch processing started.");
	}

}
