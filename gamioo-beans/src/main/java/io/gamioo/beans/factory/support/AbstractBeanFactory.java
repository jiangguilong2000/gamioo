package io.gamioo.beans.factory.support;

import io.gamioo.beans.config.BeanDefinition;
import io.gamioo.beans.factory.BeanFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanFactory implements BeanFactory {

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();



    public <T> T getBean(Class<T> requiredType){
        return (T)beanDefinitionMap.get(StringUtils.uncapitalize(requiredType.getSimpleName())).getBean();
    }

    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception {
        // 何时设置beanDefinition的其他属性beanClass,beanClassName？——在BeanDefinitionReader加载xml文件的时候set（初始化的时候）
        //测试用例指定要获取的beanClassName
        Object bean = doCreateBean(beanDefinition);//beanDefinition.getBeanClass().newInstance()
        beanDefinition.setBean(bean);
        beanDefinitionMap.put(name, beanDefinition);
    }

    abstract Object doCreateBean(BeanDefinition beanDefinition) throws Exception;

}