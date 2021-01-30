package io.gamioo.event.guava;

import com.google.common.eventbus.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserEventHandler {
	private static final Logger logger = LogManager.getLogger(UserEventHandler.class);
	private static UserEventHandler instance;
	
	public static UserEventHandler getInstance() {
		if(instance==null) {
			instance=new UserEventHandler();
		}
		return instance;
	}
	
	@Subscribe
	public void handle(UserLoginEvent event) {
		logger.debug("UserEventHandler.handle(UserLoginEvent event)");
	}
	

	//可以监听到父类事件
	@Subscribe
	public void handle(BaseEvent event) {
		logger.debug("UserEventHandler.handle(BaseEvent event)");
	}

}
