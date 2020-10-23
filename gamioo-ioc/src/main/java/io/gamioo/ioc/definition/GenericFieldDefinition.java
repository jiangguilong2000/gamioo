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

import io.gamioo.core.util.FieldUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class GenericFieldDefinition implements FieldDefinition {
    private Field field;


    public GenericFieldDefinition(Field field) {
        this.field = field;
     //   this.field.setAccessible(true);
    }


    public Annotation[] getAnnotationList() {
        return this.field.getDeclaredAnnotations();
    }

    @Override
    public Class<?> getClazz() {
        Class<?>  clazz= field.getType().getClass();
        return clazz;
    }

    @Override
    public Field getField() {
        return field;
    }


    @Override
    public String getName() {
        return this.field.getName();
    }

    /**
     * 注入对象.
     *
     * @param instance 宿主对象
     */
    @Override
    public void inject(Object instance,Object value) {

        //TODO ...

        FieldUtils.writeField(instance, field, value);
    }


    /**
     * 获取此方法的访问入口所对应的Index.
     *
     * @return 访问入口所对应的Index.
     */
    @Override
    public int getIndex() {
        return 0;
    }


}


