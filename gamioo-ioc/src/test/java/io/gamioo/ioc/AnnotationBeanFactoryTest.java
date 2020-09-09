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

import io.gamioo.ioc.context.AnnotationConfigApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;


/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */

@DisplayName("IOC测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AnnotationBeanFactoryTest {
    private static final Logger logger = LogManager.getLogger(AnnotationBeanFactoryTest.class);
    //private final Benchmark benchmark=new Benchmark(10000);
    private static AnnotationConfigApplicationContext context;

    @BeforeAll
    public static void beforeAll() throws Exception {


        //初始化......
        context=new AnnotationConfigApplicationContext(AnnotationBeanFactoryTest.class.getPackage().getName());

    }

    @Test
    @Order(1)
    @DisplayName("IOC2测试")
    public void test() throws Exception {



//        //初始化完毕，获取想要的bean
        HelloWorldService helloWorldService = context.getBean(HelloWorldService.class);
        logger.debug("content={}", helloWorldService.helloWorld());

    }


    @BeforeEach
    public void beforeEach() {

    }

    @AfterEach
    public void afterEach() {

    }


    @AfterAll
    public static void afterAll() {

    }


}
