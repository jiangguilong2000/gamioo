package io.gamioo.beans.factory;

import io.gamioo.beans.config.BeanDefinition;

public interface BeanFactory {
    public <T> T getBean(Class<T> requiredType);

    void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception;
}