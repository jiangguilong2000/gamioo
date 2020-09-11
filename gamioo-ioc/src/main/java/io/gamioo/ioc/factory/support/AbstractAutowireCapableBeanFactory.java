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

package io.gamioo.ioc.factory.support;


import io.gamioo.core.util.ClassUtils;
import io.gamioo.ioc.PropertyValue;
import io.gamioo.ioc.beans.BeanWrapper;
import io.gamioo.ioc.beans.BeanWrapperImpl;
import io.gamioo.ioc.config.BeanDefinition;

import java.lang.reflect.Field;


/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {

    @Override
    protected BeanWrapper createBeanInstance(BeanDefinition beanDefinition) {
        return instantiateBean(beanDefinition);
    }

    /**初始化BEAN*/
    protected BeanWrapper instantiateBean(BeanDefinition beanDefinition) {
        Object object = ClassUtils.newInstance(beanDefinition.getBeanClass());
        BeanWrapper ret = new BeanWrapperImpl(object);
        return ret;
    }

    /**
     * 填充bean
     */
    @Override
    protected void populateBean(BeanWrapper beanWrapper,BeanDefinition beanDefinition) {
        try {
            applyPropertyValues(beanWrapper, beanDefinition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    Object doCreateBean(BeanDefinition beanDefinition) throws Exception {
//        Object bean = beanDefinition.getBeanClass().newInstance();
//        beanDefinition.setBean(bean);
//        applyPropertyValues(bean, beanDefinition);
//        return bean;
//    }

    void applyPropertyValues(BeanWrapper beanWrapper, BeanDefinition beanDefinition) throws Exception {
        for (PropertyValue propertyValue : beanDefinition.getPropertyValues().getPropertyValues()) {
            Field field =  beanDefinition.getBeanClass().getDeclaredField(propertyValue.getName());
            field.setAccessible(true);
            field.set(beanWrapper.getWrappedInstance(), propertyValue.getValue());
        }
    }

}