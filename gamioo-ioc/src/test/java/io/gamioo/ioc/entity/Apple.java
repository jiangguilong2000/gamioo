package io.gamioo.ioc.entity;

import io.gamioo.ioc.factory.annotation.Autowired;
import io.gamioo.ioc.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
@Component
public class Apple extends Fruit{

    @Autowired
    private List<Fruit> list;

    @Autowired
    private Pig pig;

    @Autowired
    private Map<String,Person> map;

    @PostConstruct
    public void init() {
        this.name = "apple";
        System.out.println("apple init");
    }

    public Pig getPig() {
        return pig;
    }

    public void setPig(Pig pig) {
        this.pig = pig;
    }

    public List<Fruit> getList() {
        return list;
    }

    public void setList(List<Fruit> list) {
        this.list = list;
    }

}
