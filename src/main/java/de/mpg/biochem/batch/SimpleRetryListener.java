package de.mpg.biochem.batch;

import org.apache.log4j.Logger;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;



/**
 * Simple Repeat Listener listening on retry operations.
 */
public class SimpleRetryListener implements RetryListener {

	private Logger logger = Logger.getLogger(SimpleRetryListener.class);
	
	/**
	 * Called after the final attempt (successful or not).
	 */
	@Override
	public <T> void close(RetryContext retryContext, RetryCallback<T> retryCallback, Throwable throwable) {
		//logger.info("No more retry attempts...");
	}

	/**
	 * Called after every unsuccessful attempt at a retry.
	 */
	@Override
	public <T> void onError(RetryContext retryContext, RetryCallback<T> retryCallback, Throwable throwable) {
		logger.info("Unsuccessful call...");
	}

	/**
	 * Called before the first attempt in a retry.
	 */
	@Override
	public <T> boolean open(RetryContext retryContext, RetryCallback<T> retryCallback) {
		//logger.info("Attempting call...");
		return true;
	}

}
