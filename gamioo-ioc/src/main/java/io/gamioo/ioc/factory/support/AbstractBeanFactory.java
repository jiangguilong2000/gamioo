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

import io.gamioo.ioc.beans.BeanWrapper;
import io.gamioo.ioc.config.BeanDefinition;
import io.gamioo.ioc.factory.BeanFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public abstract class AbstractBeanFactory implements BeanFactory {
    private static final Logger logger = LogManager.getLogger(AbstractBeanFactory.class);
    protected final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(128);

    /** Cache of singleton objects: bean name --> bean instance */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>(128);
    
    @Override
    public <T> T getBean(Class<T> requiredType){
        return (T)singletonObjects.get(StringUtils.uncapitalize(requiredType.getSimpleName()));
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    /**实例化单例*/
    public void preInstantiateSingletons(){
        //实例化
        for (BeanDefinition e : beanDefinitionMap.values()) {
            try {
                BeanWrapper  wrapper = createBeanInstance(e);
                populateBean(wrapper,e);
                singletonObjects.put(e.getBeanClassName(),wrapper.getWrappedInstance());
//                beanDefinitionMap.put(e.getBeanClassName(), e);
            } catch (Exception ex) {
                logger.error(ex.getMessage(),ex);
            }

        }
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

    abstract BeanWrapper createBeanInstance(BeanDefinition beanDefinition) throws Exception;

    abstract  void populateBean(BeanWrapper beanWrapper,BeanDefinition beanDefinition);

}