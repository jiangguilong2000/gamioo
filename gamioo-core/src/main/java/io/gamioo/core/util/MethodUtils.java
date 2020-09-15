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
package io.gamioo.core.util;


import io.gamioo.core.exception.NoPublicMethodException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 方法工具类.
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class MethodUtils {

    /**
     * 强制调用一个方法{@link Method}.
     *
     * @param target 目标对象
     * @param method 要调用的方法
     * @param args   方法参数
     * @return 返回方法的返回值
     */
    public static Object invoke(final Object target, final Method method, final Object... args) {
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("反射调用方式时出现了异常情况...", e);
        }
    }

    /**
     * 获取指定类的所有方法，包含父类的方法.
     *
     * @param klass 指定类
     * @return 指定类的方法集合.
     */
    public static List<Method> getAllMethod(final Class<?> klass) {
        Set<Method> result = new HashSet<>();
        for (Class<?> target = klass; target != Object.class; target = target.getSuperclass()) {
            for (Method method : target.getDeclaredMethods()) {
                result.add(method);
            }
        }
        return new ArrayList<>(result);
    }

    /**
     * 获取指定类中的指定名称和参数的方法
     *
     * @param klass          类
     * @param name           方法名称
     * @param parameterTypes 方法参数
     * @return 方法
     */
    public static Method getMethod(Class<?> klass, String name, Class<?>... parameterTypes) {
        try {
            return klass.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new NoPublicMethodException(e.getMessage());
        }
    }

    /**
     * 判定指定类是否存在Set方法.
     *
     * @param klass 指定类
     * @return 如果存在则返回true, 否则返回false.
     */
    public static boolean existSetMethod(Class<?> klass) {
        for (Class<?> target = klass; target != Object.class; target = target.getSuperclass()) {
            for (Method method : target.getDeclaredMethods()) {
                if (method.getName().startsWith("set")) {
                    return true;
                }
            }
        }
        return false;
    }
}