package io.gamioo.sandbox.asm;

import com.esotericsoftware.reflectasm.MethodAccess;

import java.lang.reflect.Method;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class ASMTest {
    public static void main(String[] args) {
        Fruit fruit = new Fruit();
        MethodAccess access = MethodAccess.get(Fruit.class);
        Method[] list = Fruit.class.getDeclaredMethods();
        for (Method e : list) {
            int index = access.getIndex(e.getName(), e.getParameterTypes());
            System.out.println(index);
        }
        Object obj = access.invoke(fruit, "hello", "allen");
        System.out.println(obj);

    }
}
