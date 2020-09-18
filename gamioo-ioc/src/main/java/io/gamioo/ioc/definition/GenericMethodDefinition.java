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

package io.gamioo.ioc.definition;

import com.esotericsoftware.reflectasm.MethodAccess;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 通用的法的基本定义类
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class GenericMethodDefinition implements MethodDefinition {
    private int index;
    private Method method;
    private MethodAccess access;
    private Parameter[] parameters;


    public GenericMethodDefinition(MethodAccess access, Method method) {
        this.method = method;
        this.access = access;
        this.index = access.getIndex(method.getName(), method.getParameterTypes());
        this.parameters = method.getParameters();
        this.method.setAccessible(true);
    }


    public Annotation[] getAnnotationList() {
        return this.method.getDeclaredAnnotations();
    }

    @Override
    public Class<?> getClazz() {
        return method.getGenericReturnType().getClass();
    }

    @Override
    public String getName() {
        return this.method.getName();
    }

    /**
     * 获取此方法的访问入口.
     *
     * @return 访问入口
     */
    @Override
    public MethodAccess getMethodAccess() {
        return access;
    }

    /**
     * 获取此方法的访问入口所对应的Index.
     *
     * @return 访问入口所对应的Index.
     */
    @Override
    public int getIndex() {
        return index;
    }


    public Object invoke(Object instance,Object ...args) {
        Object ret = null;
        if (args == null) {
            // 无参数调用
            ret = access.invoke(instance, index);
        } else {
            // 有参数调用
            ret = access.invoke(instance, index, args);
        }
        return ret;
    }



    /**
     * 获取参数列表
     *
     * @return 参数
     */
    @Override
    public Parameter[] getParameters() {
        return parameters;
    }
}
