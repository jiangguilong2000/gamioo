package io.gamioo.beans.factory.support;

import io.gamioo.beans.PropertyValue;
import io.gamioo.beans.config.BeanDefinition;

import java.lang.reflect.Field;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {

    @Override
    Object doCreateBean(BeanDefinition beanDefinition) throws Exception {
        Object bean = beanDefinition.getBeanClass().newInstance();
        beanDefinition.setBean(bean);
        applyPropertyValues(bean, beanDefinition);
        return bean;
    }

    void applyPropertyValues(Object bean, BeanDefinition mdb) throws Exception {
        for (PropertyValue propertyValue : mdb.getPropertyValues().getPropertyValues()) {
            Field field = bean.getClass().getDeclaredField(propertyValue.getName());
            field.setAccessible(true);
            field.set(bean, propertyValue.getValue());
        }
    }

}