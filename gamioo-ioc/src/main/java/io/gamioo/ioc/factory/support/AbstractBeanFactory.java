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
import io.gamioo.core.exception.ServerBootstrapException;
import io.gamioo.core.util.StringUtils;
import io.gamioo.ioc.definition.BeanDefinition;
import io.gamioo.ioc.factory.BeanFactory;
import io.gamioo.ioc.factory.ObjectFactory;
import io.gamioo.ioc.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public abstract class AbstractBeanFactory implements BeanFactory {
    private static final Logger logger = LogManager.getLogger(AbstractBeanFactory.class);
    /**
     * Map of bean definition objects, keyed by bean name.
     */
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

    /**
     * Set of registered singletons, containing the bean names in registration order.
     */
    private final Set<String> registeredSingletons = new LinkedHashSet<>(256);

    /**
     * Names of beans that are currently in creation.
     */
    private final Set<String> singletonsCurrentlyInCreation =
            Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<T> requiredType) {
        return (T) singletonObjects.get(StringUtils.uncapitalized(requiredType.getSimpleName()));
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(StringUtils.uncapitalized(beanName), beanDefinition);
    }

    /**
     * 实例化单例
     */
    @Override
    public void preInstantiateSingletons() {
        for (BeanDefinition e : beanDefinitionMap.values()) {
            getBean(e.getName());
        }
    }


    @Override
    public Object getBean(String name) throws BeansException {
        return doGetBean(name);

    }


    @SuppressWarnings("unchecked")
    protected <T> T doGetBean(String name) throws BeansException {
        BeanDefinition beanDefinition = beanDefinitionMap.get(name);
        // Eagerly check singleton cache for manually registered singletons.
        Object sharedInstance = getSingleton(name);
        if (sharedInstance == null) {
            sharedInstance = this.getSingleton(name, () -> createBean(beanDefinition));
        }
        return (T) sharedInstance;
    }

    @Override
    public <T> List<T> getBeanListOfType(Class<T> type) {
        List<T> ret = new ArrayList<>();
        Collection<BeanDefinition> list = beanDefinitionMap.values();
        for (BeanDefinition e : list) {
            if (type.isAssignableFrom(e.getClazz())) {
                T instance = (T) this.getBean(e.getName());
                ret.add(instance);
            }
        }
        return ret;
    }

    @Override
    public <T> Map<String,T> getBeanMapOfType(Class<T> type) {
        Map<String,T> ret = new HashMap<>();
        Collection<BeanDefinition> list = beanDefinitionMap.values();
        for (BeanDefinition e : list) {
            if (type.isAssignableFrom(e.getClazz())) {
                T instance = (T) this.getBean(e.getName());
                Component annotation =(Component)e.getAnnotationList()[0];
                ret.put(annotation.name()[0],instance);
            }
        }
        return ret;
    }



    @Override
    public <T> T getBean(String name, Class<T> requiredType){
        Object bean = getBean(name);
        if (requiredType != null && !requiredType.isInstance(bean)) {
            throw new ServerBootstrapException(name, requiredType, bean.getClass());
        }
        return (T) bean;
    }



//    public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
//        Assert.notNull(beanName, "Bean name must not be null");
//        Assert.notNull(singletonObject, "Singleton object must not be null");
//        synchronized (this.singletonObjects) {
//            Object oldObject = this.singletonObjects.get(beanName);
//            if (oldObject != null) {
//                throw new IllegalStateException("Could not register object [" + singletonObject +
//                        "] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
//            }
//            addSingleton(beanName, singletonObject);
//        }
//    }

    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }


    /**
     * Add the given singleton factory for building the specified singleton
     * if necessary.
     * <p>To be called for eager registration of singletons, e.g. to be able to
     * resolve circular references.
     *
     * @param beanName         the name of the bean
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
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
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
                beforeSingletonCreation(beanName);
                singletonObject = singletonFactory.getObject();
                afterSingletonCreation(beanName);
                addSingleton(beanName, singletonObject);
            }
            return singletonObject;

        }

    }

    public void destroySingleton(String beanName) {
        // Remove a registered singleton of the given name, if any.
        removeSingleton(beanName);
    }

    /**
     * Remove the bean with the given name from the singleton cache of this factory,
     * to be able to clean up eager registration of a singleton if creation failed.
     *
     * @param beanName the name of the bean
     */
    protected void removeSingleton(String beanName) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.remove(beanName);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.remove(beanName);
        }
    }

    /**
     * Return whether the specified singleton bean is currently in creation
     * (within the entire factory).
     *
     * @param beanName the name of the bean
     */
    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    /**
     * Callback before singleton creation.
     * <p>The default implementation register the singleton as currently in creation.
     *
     * @param beanName the name of the singleton about to be created
     * @see #isSingletonCurrentlyInCreation
     */
    protected void beforeSingletonCreation(String beanName) {
        this.singletonsCurrentlyInCreation.add(beanName);

    }

    /**
     * Callback after singleton creation.
     * <p>The default implementation marks the singleton as not in creation anymore.
     *
     * @param beanName the name of the singleton that has been created
     * @see #isSingletonCurrentlyInCreation
     */
    protected void afterSingletonCreation(String beanName) {
        this.singletonsCurrentlyInCreation.remove(beanName);
    }


    protected Object getEarlyBeanReference(Object bean) {
        return bean;
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

    abstract Object createBeanInstance(BeanDefinition beanDefinition);

    abstract void populateBean(Object instance, BeanDefinition beanDefinition);

    abstract void initializeBean(Object instance, BeanDefinition beanDefinition);

}