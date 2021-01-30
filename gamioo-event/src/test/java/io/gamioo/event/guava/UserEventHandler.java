package io.gamioo.event.guava;

import com.google.common.eventbus.Subscribe;

public class UserEventHandler {

	private static UserEventHandler instance;
	
	public static UserEventHandler getInstance() {
		if(instance==null) {
			instance=new UserEventHandler();
		}
		return instance;
	}
	
	@Subscribe
	public void handle(UserLoginEvent event) {
		System.out.println(Thread.currentThread().getName()+ "-UserEventHandler.handle(UserLoginEvent event)");
	}
	

	
	@Subscribe
	public void handle(BaseEvent event) {
		System.out.println(Thread.currentThread().getName()+ "-UserEventHandler.handle(BaseEvent event)");
	}
//	 @Subscribe
//	 public void onEvent(DeadEvent event) throws Exception {
//		 System.out.println("未有监听的");
//		// throw new Exception("");
//		// System.out.println(event);
//	 }
}
