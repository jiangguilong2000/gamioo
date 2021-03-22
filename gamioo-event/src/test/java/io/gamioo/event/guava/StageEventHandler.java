package io.gamioo.event.guava;

import com.google.common.eventbus.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StageEventHandler {
	private static final Logger logger = LogManager.getLogger(StageEventHandler.class);

	private static StageEventHandler instance;
	
	public static StageEventHandler getInstance() {
		if(instance==null) {
			instance=new StageEventHandler();
		}
		return instance;
	}
	
	@Subscribe
	public void handle(UserLoginEvent event) {
		logger.debug("StageEventHandler.handle(UserLoginEvent event)");
	}
}
