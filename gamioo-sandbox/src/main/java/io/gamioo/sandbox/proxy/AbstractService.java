package io.gamioo.sandbox.proxy;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Proxy;

public abstract class AbstractService {
    private static final Logger LOGGER = LogManager.getLogger(AbstractService.class);

    private MethodAccess access;


    protected Object getProxy(Object id, Object object) {
        // 类加载器
        ClassLoader classLoader = object.getClass().getClassLoader();
        // 被代理对象实现的接口
        Class<?>[] interfaces = object.getClass().getInterfaces();
        access = MethodAccess.get(object.getClass());

        // new InvocationHandler() 执行的方法
        Object o = Proxy.newProxyInstance(classLoader, interfaces, (proxy, method, args) -> {
            //    Executor executor = this.businessExecutor.getExecutor(id);
            //TODO ...这里改成 fibber
            //      executor.execute(() -> {
           
            System.out.println(id);
            Object invoke = method.invoke(object, args);

            return invoke;
        });

        return o;
    }

}
