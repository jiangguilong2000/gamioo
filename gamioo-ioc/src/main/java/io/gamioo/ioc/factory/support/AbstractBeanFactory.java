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
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public abstract class AbstractBeanFactory implements BeanFactory {

    private final Map<String, AbstractBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(128);



    public <T> T getBean(Class<T> requiredType){
        return (T)beanDefinitionMap.get(StringUtils.uncapitalize(requiredType.getSimpleName())).getBean();
    }

    public void registerBeanDefinition(String name, AbstractBeanDefinition beanDefinition) throws Exception {
        // 何时设置beanDefinition的其他属性beanClass,beanClassName?——在BeanDefinitionReader加载xml文件的时候set（初始化的时候）
        //测试用例指定要获取的beanClassName
        Object bean = doCreateBean(beanDefinition);//beanDefinition.getBeanClass().newInstance()
        beanDefinition.setBean(bean);
        beanDefinitionMap.put(name, beanDefinition);
    }

    abstract Object doCreateBean(AbstractBeanDefinition beanDefinition) throws Exception;

}