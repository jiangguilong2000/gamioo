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

import io.gamioo.core.util.AnnotationUtils;
import io.gamioo.core.util.Assert;
import io.gamioo.core.util.ClassUtils;
import io.gamioo.ioc.annotation.Configuration;
import io.gamioo.ioc.annotation.DefaultResourceLoader;
import io.gamioo.ioc.definition.ConfigurationBeanDefinition;
import io.gamioo.ioc.definition.GenericBeanDefinition;
import io.gamioo.ioc.factory.BeanFactory;
import io.gamioo.ioc.io.Resource;
import io.gamioo.ioc.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class ClassPathBeanDefinitionScanner {
    private static final Logger logger = LogManager.getLogger(ClassPathBeanDefinitionScanner.class);

    private DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
    private BeanFactory beanFactory;

    public ClassPathBeanDefinitionScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        //    this.resourceLoader = resourceLoader;
    }

    public void doScan(String... basePackages) {
        Assert.notEmpty(basePackages, "At least one base package must be specified");
        for (String e : basePackages) {
             this.analysisResourceList(e);
        }
    }


    /**
     * 解析加载进来的资源
     *
     * @param location 资源的地址
     */
    public void analysisResourceList(String location){
        List<Resource> list = this.resourceLoader.getResourceList(location);
        for (Resource e : list) {
            this.analysisResource(e);
        }
    }

    public void analysisResource(Resource resource)  {
            String className = resource.getClassName();
            Class<?> clazz = ClassUtils.loadClass(className);
            //TODO ...
            analysisClass(clazz);
    }

    /**
     * 解析Class，处理游戏定义
     *
     * @param klass Class
     */
    private void analysisClass(Class<?> klass) {
        // 接口、内部类、枚举、注解和匿名类 直接忽略
        if (klass.isInterface() || klass.isMemberClass() || klass.isEnum() || klass.isAnnotation() || klass.isAnonymousClass()) {
            return;
        }

        // 抽象类和非Public的也忽略
        int modify = klass.getModifiers();
        if (Modifier.isAbstract(modify) || (!Modifier.isPublic(modify))) {
            return;
        }

        // 查找带有@Component注解或其他注解上有@Component注解的类
        Annotation annotation = AnnotationUtils.getAnnotation(klass, Component.class);
        if (annotation == null) {
            return;
        }

        // 配置类
        if (annotation.annotationType() == Configuration.class) {
           // configurations.add(new ConfigurationBeanDefinition(klass,annotation));
            beanFactory.registerBeanDefinition(klass.getSimpleName() ,new ConfigurationBeanDefinition(klass,annotation));
        }else{
            //不需要处理循环引用的问题，只要不同的定义放到不同的集合里就行
            beanFactory.registerBeanDefinition(klass.getSimpleName() ,new GenericBeanDefinition(klass,annotation));
        }





//        // 模板转化器.
//        else if (annotationType == TemplateConverter.class) {
//            ConvertManager.getInstance().register(klass, (TemplateConverter) annotation);
//        }
//        // 协议入口控制类
//        else if (annotationType == Controller.class) {
//            analytical(klass, (Controller) annotation);
//        }
//        // 协议入口控制类(模块化)
//        else if (annotationType == ModuleController.class) {
//            analytical(klass, (ModuleController) annotation);
//        }
//        // 静态组件
//        else if (annotationType == StaticComponent.class) {
//            staticComponents.add(new StaticComponentBeanDefinition(klass).init());
//        }
//        // 不是已定义的，那就扫描这个注解上有没有@Component
//        else {
//            beans.put(klass, new DefaultBeanDefinition(klass, annotation, annotationType).init());
//        }
    }
}
