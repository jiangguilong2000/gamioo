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

package io.gamioo.ioc.context;

import io.gamioo.ioc.annotation.AnnotationBeanDefinitionReader;
import io.gamioo.ioc.factory.support.DefaultListableBeanFactory;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class AnnotationConfigApplicationContext  implements  ApplicationContext{


    private String location;
    private DefaultListableBeanFactory beanFactory;

    public AnnotationConfigApplicationContext(String location){
        this.location=location;
        this.refresh();
    }

    /**TODO ...*/
    public void refresh(){
    //注入属性实例化等
        DefaultListableBeanFactory factory=this.refreshBeanFactory();
        prepareBeanFactory(factory);
        // Instantiate all remaining (non-lazy-init) singletons.
        finishBeanFactoryInitialization(factory);

    }
    public DefaultListableBeanFactory refreshBeanFactory(){
        DefaultListableBeanFactory factory=this.createBeanFactory();
        this.loadBeanDefinitions(factory);
        return factory;
    }

    /**TODO ...做一些beanFactory的属性设置工作*/
    protected void prepareBeanFactory(DefaultListableBeanFactory beanFactory) {

    }

    /**实例化操作*/
    protected void finishBeanFactoryInitialization(DefaultListableBeanFactory beanFactory){
        beanFactory.preInstantiateSingletons();
    }

    public String getLocation() {
        return location;
    }

    @Override
    public DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory();
    }

    @Override
    public void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        this.beanFactory=beanFactory;
        AnnotationBeanDefinitionReader reader = new AnnotationBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(location);

    }

    public <T> T getBean(Class<T> requiredType){
        return this.beanFactory.getBean(requiredType);
    }

//    public class AnnotationConfigApplicationContext extends GenericApplicationContext implements AnnotationConfigRegistry {
//
//        private final AnnotatedBeanDefinitionReader reader;
//
//        private final ClassPathBeanDefinitionScanner scanner;


    }
