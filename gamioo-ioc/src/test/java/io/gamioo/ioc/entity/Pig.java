package io.gamioo.ioc.entity;


import io.gamioo.ioc.factory.annotation.Autowired;
import io.gamioo.ioc.stereotype.Component;

@Component
public class Pig {
    @Autowired
    public Fruit fruit;
    
    private int id;
    private String name;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    
    
}
