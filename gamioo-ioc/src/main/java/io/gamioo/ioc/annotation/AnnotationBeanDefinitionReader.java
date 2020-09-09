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

package io.gamioo.ioc.annotation;

import io.gamioo.core.util.AnnotationUtils;
import io.gamioo.core.util.ClassUtils;
import io.gamioo.ioc.factory.support.AbstractBeanDefinition;
import io.gamioo.ioc.factory.support.AbstractBeanDefinitionReader;
import io.gamioo.ioc.factory.support.DefaultListableBeanFactory;
import io.gamioo.ioc.io.Resource;
import io.gamioo.ioc.io.ResourceLoader;
import io.gamioo.ioc.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class AnnotationBeanDefinitionReader extends AbstractBeanDefinitionReader {
    private static final Logger logger = LogManager.getLogger(AnnotationBeanDefinitionReader.class);
    public AnnotationBeanDefinitionReader(DefaultListableBeanFactory beanFactory) {
        super(beanFactory);
    }



    public void loadBeanDefinitions(){

        ResourceLoader resourceLoader=this.getResourceLoader();
        //扫描资源
        this.scanPackage(resourceLoader.getLocation(), "io.gamioo");
        //实例化
        for (Map.Entry<String, AbstractBeanDefinition> beanDefinitionEntry : this.getRegistry().entrySet()) {
            this.getBeanFactory().registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
        }

    }

    public  void scanPackage(String... packages)  {
        for (String e : packages) {
            try {
                this.analysisResourceList(e);
            } catch (IOException ex) {
                logger.error(ex.getMessage(),ex);
            }
        }
    }

    /**
     * 解析加载进来的资源
     *
     * @param location 资源的地址
     */
    @Override
    public void analysisResourceList(String location) throws IOException {
        List<Resource> list = this.getResourceLoader().getResourceList(location);
        for (Resource e : list) {
            this.analysisResource(e);
        }
    }

    public void analysisResource(Resource resource) throws IOException {

        String className = resource.getClassName();
        Class<?>  clazz=ClassUtils.loadClass(className);
        //TODO ...
        parseClass(clazz);

    }

    /**
     * 解析Class
     *
     * @param klass Class
     */
    private void parseClass(Class<?> klass) {
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

//        // 目标类上实际注解类型
//        Class<? extends Annotation> annotationType = annotation.annotationType();
//        // 配置类
//        if (annotationType == Configuration.class) {
//            configurations.add(new ConfigurationBeanDefinition(klass).init());
//        }
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







//    public Set<GenericBeanDefinition> doScan(String basePackage) {
//        List<Resource> list= this.getResources(basePackage);
//
//
//        return null;
//    }
}
