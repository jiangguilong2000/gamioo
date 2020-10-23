package io.gamioo.ioc.entity;


import io.gamioo.ioc.factory.annotation.Autowired;
import io.gamioo.ioc.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class Pig {
    @Autowired
    public List<Fruit> fruitList;
    
    private int id;
    private String name;

    @PostConstruct
	public void init(){
		System.out.println("pig init");
	}
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
