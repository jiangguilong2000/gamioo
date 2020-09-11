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


import io.gamioo.ioc.factory.BeanFactory;
import io.gamioo.ioc.io.ResourceLoader;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {

   // private Map<String, AbstractBeanDefinition> registry= new HashMap<String, AbstractBeanDefinition>();

    private ResourceLoader resourceLoader;

    private BeanFactory beanFactory;

    public AbstractBeanDefinitionReader(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

//    public Map<String, AbstractBeanDefinition> getRegistry() {
//        return registry;
//    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}