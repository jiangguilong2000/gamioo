package io.gamioo.event.guava;

import com.google.common.eventbus.Subscribe;

public class StageEventHandler {


	private static StageEventHandler instance;
	
	public static StageEventHandler getInstance() {
		if(instance==null) {
			instance=new StageEventHandler();
		}
		return instance;
	}
	
	@Subscribe
	public void handle(UserLoginEvent event) {
		System.out.println(Thread.currentThread().getName()+ "-StageEventHandler.handle(UserLoginEvent event)");
	}
	

	
	

}
