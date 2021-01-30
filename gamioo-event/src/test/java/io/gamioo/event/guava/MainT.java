package io.gamioo.event.guava;

public class MainT {

	public static void main(String[] args) {
		UserLoginEvent event=new UserLoginEvent();
		event.subscribe();
		for(int i=0;i<100;i++) {
			event.publish("Allen");
		}
	}

}
