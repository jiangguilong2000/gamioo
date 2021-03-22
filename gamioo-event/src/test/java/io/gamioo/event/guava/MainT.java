package io.gamioo.event.guava;

public class MainT {

	public static void main(String[] args) {
		UserLoginEvent event=new UserLoginEvent();
		event.subscribe();
		event.publish("Allen");
	}

}
