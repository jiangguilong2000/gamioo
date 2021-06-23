package io.gamioo.sandbox.proxy.buddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class Client {
    private static final Logger logger = LogManager.getLogger(Client.class);

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        Singer proxy = client.createByteBuddyDynamicProxy(Singer.class);
        String result = proxy.sing("allen");
        proxy.fun1();
        // System.out.println(ret);
        //  System.out.println(proxy.toString());
    }
//    public <T> T getProxy(Class<T> clazz) {
//            ClassLoader classLoader = clazz.getClassLoader();
//            // 被代理对象实现的接口
//            Class<?>[] interfaces = clazz.getInterfaces();
//            try {
//                final T service = clazz.newInstance();
//                // 动态代理
//                proxy = Proxy.newProxyInstance(classLoader, interfaces, (proxy, method, args) ->
//                );
//
//            } catch (Exception e) {
//                logger.error(e.getMessage(), e);
//            }
//
//        return (T) proxy;
//    }


    private <T> T createByteBuddyDynamicProxy(Class<T> clazz) throws Exception {

        return (T) new ByteBuddy()
                .with(new NamingStrategy.SuffixingRandom("Access"))
                .subclass(clazz)

                // .implement(Singable.class)
                //   .implement(Singable.class)
                //  .method(ElementMatchers.not(ElementMatchers.isDeclaredBy(Object.class)))
                .method(ElementMatchers.isDeclaredBy(clazz))

                //  .intercept(MethodDelegation.to(new SingerAgentInterceptor()))
                .intercept(MethodDelegation.to(new SingerAgentInterceptor()))
                // .intercept(Advice.to(SingerAgentInterceptor.class))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded()
                .getDeclaredConstructor()
                .newInstance();
    }

    public static class SingerAgentInterceptor {
        //        @RuntimeType

        //        public Object interceptor(@This Object proxy, @Origin Method method,
//
//                                  @AllArguments Object[] args) throws Exception {
//            System.out.println("bytebuddy delegate proxy2 before sing ");
//            Object ret = method.invoke(proxy, args);
//            System.out.println("bytebuddy delegate proxy2 after sing ");
//            return ret;
//        }
        @RuntimeType
        public Object interceptor(@This Object proxy, @Origin Method method, @SuperCall Callable<Object> call) throws Exception {
            System.out.println("bytebuddy delegate proxy2 before sing ");
            // Object ret = superMethod.invoke(proxy, args);
            Object ret = call.call();
            System.out.println("bytebuddy delegate proxy2 after sing ");
            return ret;
        }


//
//        public static Object intercept(@SuperCall Callable<?> zuper, @Origin Method method,
//                                       @AllArguments Object[] args, @This Object proxy) throws Exception {
//            System.out.println("CURRENT: " + method.getDeclaringClass().getName());
//
//            return zuper.call();
//        }
//
//        @OnMethodEnter
//        public static void onMethodEnter(@AllArguments Object[] arguments, @Origin Method method) {
//
//            System.out.println("Enter " + method.getName() + " with arguments: " + Arrays.toString(arguments));
//
//        }
//
//        @OnMethodExit
//        public static void onMethodExit(@AllArguments Object[] arguments, @Origin Method method, @Return Object returned) {
//
//            System.out.println("Exit " + method.getName() + " with arguments: " + Arrays.toString(arguments) + " return: " + returned);
//
//        }

    }
}
