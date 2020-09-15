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

import io.gamioo.core.exception.BeansException;
import io.gamioo.ioc.beans.BeanWrapper;
import io.gamioo.ioc.config.BeanDefinition;
import io.gamioo.ioc.factory.BeanFactory;
import io.gamioo.ioc.factory.ObjectFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public abstract class AbstractBeanFactory implements BeanFactory {
    private static final Logger logger = LogManager.getLogger(AbstractBeanFactory.class);
    /** Map of bean definition objects, keyed by bean name. */
    protected final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(128);
    /**
     * Cache of singleton objects: bean name --> bean instance 一级缓存
     */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
    /**
     * Cache of early singleton objects: bean name --> bean instance  二级缓存
     */
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    /**
     * Cache of singleton factories: bean name --> ObjectFactory 三级缓存
     */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

    /** Set of registered singletons, containing the bean names in registration order. */
    private final Set<String> registeredSingletons = new LinkedHashSet<>(256);


    @Override
    public <T> T getBean(Class<T> requiredType) {
        return (T) singletonObjects.get(StringUtils.uncapitalize(requiredType.getSimpleName()));
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    /**
     * 实例化单例
     */
    @Override
    public void preInstantiateSingletons() {
        //实例化
        for (BeanDefinition e : beanDefinitionMap.values()) {
            try {
              getBean(e.getBeanClassName());

            //    singletonObjects.put(e.getBeanClassName(), wrapper.getWrappedInstance());
                beanDefinitionMap.put(e.getBeanClassName(), e);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

        }
    }


    @Override
    public Object getBean(String name) throws BeansException {
        return doGetBean(name);

    }

    protected <T> T doGetBean(String name) throws BeansException {
        BeanDefinition beanDefinition = beanDefinitionMap.get(name);
        // Eagerly check singleton cache for manually registered singletons.
        Object sharedInstance = getSingleton(name);
        if (sharedInstance == null) {
            getSingleton(name, () -> {
                return createBean(beanDefinition);
            });
        }
        return null;
    }


    /**
     * Add the given singleton factory for building the specified singleton
     * if necessary.
     * <p>To be called for eager registration of singletons, e.g. to be able to
     * resolve circular references.
     * @param beanName the name of the bean
     * @param singletonFactory the factory for the singleton object
     */
    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        synchronized (this.singletonObjects) {
            if (!this.singletonObjects.containsKey(beanName)) {
                this.singletonFactories.put(beanName, singletonFactory);
                this.earlySingletonObjects.remove(beanName);
                this.registeredSingletons.add(beanName);
            }
        }
    }
    /**
     * 获取单例
     */
    protected Object getSingleton(String beanName) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            synchronized (this.singletonObjects) {
                singletonObject = this.earlySingletonObjects.get(beanName);
                if (singletonObject == null) {
                    ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                    if (singletonFactory != null) {


                        singletonObject = singletonFactory.getObject();

                        this.earlySingletonObjects.put(beanName, singletonObject);
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }
        return singletonObject;
    }


    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        synchronized (this.singletonObjects) {
            Object singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
                singletonObject = singletonFactory.getObject();
            }


        }

        return null;
    }

    //
//    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
//        // 何时设置beanDefinition的其他属性beanClass,beanClassName?——在BeanDefinitionReader加载xml文件的时候set（初始化的时候）
//        //测试用例指定要获取的beanClassName
//        Object bean = null;//beanDefinition.getBeanClass().newInstance()
//        try {
//            bean = doCreateBean(beanDefinition);
//            beanDefinition.setBean(bean);
//            beanDefinitionMap.put(name, beanDefinition);
//        } catch (Exception e) {
//            logger.error(e.getMessage(),e);
//        }
//
//    }
    abstract Object createBean(BeanDefinition beanDefinition);

    abstract BeanWrapper createBeanInstance(BeanDefinition beanDefinition);

    abstract void populateBean(BeanWrapper beanWrapper, BeanDefinition beanDefinition);

    abstract void initializeBean(BeanWrapper beanWrapper, BeanDefinition beanDefinition);

}