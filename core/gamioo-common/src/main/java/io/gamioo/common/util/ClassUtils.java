/*
 * Copyright 2015-2020 Gamioo Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gamioo.common.util;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class ClassUtils {
    /**
     * Return the default ClassLoader to use: typically the thread context
     * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
     * class will be used as fallback.
     * <p>Call this method if you intend to use the thread context ClassLoader
     * in a scenario where you clearly prefer a non-null ClassLoader reference:
     * for example, for class path resource loading (but not necessarily for
     * {@code Class.forName}, which accepts a {@code null} ClassLoader
     * reference as well).
     * @return the default ClassLoader (only {@code null} if even the system
     * ClassLoader isn't accessible)
     * @see Thread#getContextClassLoader()
     * @see ClassLoader#getSystemClassLoader()
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader ret = null;
        try {
            ret = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (ret == null) {
            // No thread context class loader -> use class loader of this class.
            ret = ClassUtils.class.getClassLoader();
            if (ret == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    ret = ClassLoader.getSystemClassLoader();
                }
                catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return ret;
    }
    /**
     * 使用当前线程的ClassLoader加载给定的类
     *
     * @param className 类的全称
     * @return 给定的类
     */
    public static Class<?> loadClass(String className) {
        // ClassLoader#loadClass(String)：将.class文件加载到JVM中，不会执行static块,只有在创建实例时才会去执行static块
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
        }

        // Class#forName(String)：将.class文件加载到JVM中，还会对类进行解释，并执行类中的static块
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
        }

        throw new RuntimeException("无法加载指定类名的Class=" + className);
    }

    /**
     * 创建一个指定类的对象,调用默认的构造函数.
     *
     * @param <T>   Class
     * @param klass 类
     * @return 指定类的对象
     */
    public static <T> T newInstance(final Class<T> klass) {
        try {
            return klass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("无法创建实例. Class=" + klass.getName(), e);
        }
    }

    /**
     * 根据ClassName和构造方法的参数列表来创建一个对象
     *
     * @param <T>        Class
     * @param className  指定类全名（包含包名称的那种）
     * @param parameters 参数列表
     * @return 指定ClassName的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className, Object... parameters) {
        Class<?> klass = (Class<?>) loadClass(className);
        try {
            Class<?>[] parameterTypes = new Class<?>[parameters.length];
            for (int i = 0, len = parameters.length; i < len; i++) {
                parameterTypes[i] = parameters[i].getClass();
            }
            return (T) klass.getConstructor(parameterTypes).newInstance(parameters);
        } catch (Exception e) {
            throw new RuntimeException("无法创建实例. Class=" + klass.getName(), e);
        }
    }

//    /**
//     * 尝试运行一个带有Main方法的类.
//     *
//     * @param mainClass 带有Main方法类的名称
//     * @param args      启动参数数组
//     */
//    public static void invokeMain(String mainClass, String[] args) {
//        final Class<?> klass = ClassUtils.loadClass(mainClass);
//        Method mainMethod = MethodUtils.getMethod(klass, "main", String[].class);
//        MethodUtils.invoke(null, mainMethod, new Object[]{args});
//    }
}
