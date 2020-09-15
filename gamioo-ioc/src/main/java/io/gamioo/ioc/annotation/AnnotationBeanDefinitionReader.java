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

import io.gamioo.ioc.context.annotation.ClassPathBeanDefinitionScanner;
import io.gamioo.ioc.factory.support.AbstractBeanDefinitionReader;
import io.gamioo.ioc.factory.support.DefaultListableBeanFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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



    public void loadBeanDefinitions(String location){

       // ResourceLoader resourceLoader=this.getResourceLoader();
        ClassPathBeanDefinitionScanner scanner=new ClassPathBeanDefinitionScanner(this.getBeanFactory());
        //扫描资源
        // Actually scan for bean definitions and register them.
       // this.getClass().getPackage().
        scanner.doScan(location, "io.gamioo");

       // this.scanPackage(resourceLoader.getLocation(), "io.gamioo");

    }









//    public Set<GenericBeanDefinition> doScan(String basePackage) {
//        List<Resource> list= this.getResources(basePackage);
//
//
//        return null;
//    }
}
