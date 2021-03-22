package io.gamioo.event.guava;

public class UserLoginEvent extends BaseEvent{

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void subscribe() {
		this.eventBus.register(UserEventHandler.getInstance());
		this.asyncEventBus.register(StageEventHandler.getInstance());
	}
	public  void publish(String name) {
		UserLoginEvent event=new UserLoginEvent();
		event.setName(name);
		this.eventBus.post(event);//同步推送
		this.asyncEventBus.post(event);//异步推送
	}
}
