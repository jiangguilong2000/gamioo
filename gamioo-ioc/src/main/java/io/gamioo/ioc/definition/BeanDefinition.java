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

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public interface BeanDefinition extends Definition {

    List<FieldDefinition> getFieldDefinitionList(Class<? extends Annotation> clazz);

     List<MethodDefinition> getMethodDefinitionList(Class<? extends Annotation> clazz);
    //  List<FieldDefinition> getValueFieldDefinition();

    Object newInstance();

    /**
     * 解析方法
     */
    void analysisMethodList();

    /**
     * 解析字段
     */
    void analysisFieldList();

    /**解析实体对象*/
    void analysisBean(Object instance);

//    /**
//     * 注入
//     */
//      void inject(Object instance);

    MethodDefinition getInitMethodDefinition();

//    void setInitMethodName(String initMethodName);
//
//    String getDestroyMethodName();
//
//    void setDestroyMethodName(String destroyMethodName);

//    String getBeanClassName();
//
//    void setBeanClassName(String beanClassName);
//
//    Class getBeanClass();


//    void setBeanClass(Class beanClass);
//
//    public PropertyValues getPropertyValues();
//
//    void setPropertyValues(PropertyValues propertyValues);

}
