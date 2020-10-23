package io.gamioo.ioc.entity;

import io.gamioo.ioc.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
@Component
public class Banana extends Fruit{

    @PostConstruct
    public void init() {
        this.name = "Banana";
        System.out.println("Banana init");
    }
}
