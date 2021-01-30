package io.gamioo.event.guava;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseEvent {
	private static final Logger logger = LogManager.getLogger(BaseEvent.class);
	private Executor executor = Executors.newCachedThreadPool();
	protected AsyncEventBus asyncEventBus = new AsyncEventBus(executor,new SubscriberExceptionHandler() {

		@Override
		public void handleException(Throwable exception, SubscriberExceptionContext context) {
			logger.error(exception.getMessage(),exception);
		}
		
		
	});
	protected EventBus eventBus = new EventBus(new SubscriberExceptionHandler() {

		@Override
		public void handleException(Throwable exception, SubscriberExceptionContext context) {
			logger.error(exception.getMessage(),exception);
		}
		
		
	});

	
	public abstract void subscribe();
}
