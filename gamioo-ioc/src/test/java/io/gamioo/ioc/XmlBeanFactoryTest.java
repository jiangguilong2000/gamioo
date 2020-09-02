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

package io.gamioo.ioc;

import io.gamioo.ioc.factory.support.AbstractBeanDefinition;
import io.gamioo.ioc.factory.support.DefaultListableBeanFactory;
import io.gamioo.ioc.factory.xml.XmlBeanDefinitionReader;
import io.gamioo.ioc.factory.xml.XmlResourceLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.util.Map;


/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */

@DisplayName("IOC测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class XmlBeanFactoryTest {
    private static final Logger logger = LogManager.getLogger(XmlBeanFactoryTest.class);
    //private final Benchmark benchmark=new Benchmark(10000);
    private static XmlBeanDefinitionReader xmlBeanDefinitionReader;

    @BeforeAll
    public  static void beforeAll() throws Exception {

        //初始化......
         xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new XmlResourceLoader());
        xmlBeanDefinitionReader.analysisResourceList("ioc.xml");

    }

    @Test
    @Order(1)
    @DisplayName("IOC2测试")
    public void test() throws Exception {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        for (Map.Entry<String, AbstractBeanDefinition> beanDefinitionEntry : xmlBeanDefinitionReader.getRegistry().entrySet()) {
            beanFactory.registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
        }
        //初始化完毕，获取想要的bean
        HelloWorldService helloWorldService =beanFactory.getBean(HelloWorldService.class);
        logger.debug("content={}",helloWorldService.helloWorld());

    }


    @BeforeEach
    public void beforeEach()  {

    }

    @AfterEach
    public void afterEach()  {

    }


    @AfterAll
    public static void afterAll()  {

    }


}
