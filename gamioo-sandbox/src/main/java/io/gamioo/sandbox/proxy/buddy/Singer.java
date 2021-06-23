package io.gamioo.sandbox.proxy.buddy;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class Singer {

    public String sing(String name) {
        System.out.println("I am singing..." + name);
        return name + "2";
    }

    public void fun1() {
        System.out.println("aaaa");
    }
}