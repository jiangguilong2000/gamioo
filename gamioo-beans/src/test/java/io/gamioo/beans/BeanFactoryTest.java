package io.gamioo.beans;

import io.gamioo.beans.config.BeanDefinition;
import io.gamioo.beans.factory.support.DefaultListableBeanFactory;
import io.gamioo.beans.factory.xml.XmlBeanDefinitionReader;
import io.gamioo.core.io.ResourceLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.util.Map;


@DisplayName("IOC测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BeanFactoryTest {
    private static final Logger logger = LogManager.getLogger(BeanFactoryTest.class);
    //private final Benchmark benchmark=new Benchmark(10000);
    private static XmlBeanDefinitionReader xmlBeanDefinitionReader;

    @BeforeAll
    public  static void beforeAll() throws Exception {

        //初始化......
         xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
        xmlBeanDefinitionReader.loadBeanDefinitions("ioc.xml");

    }

    @Test
    @Order(1)
    @DisplayName("IOC2测试")
    public void test() throws Exception {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : xmlBeanDefinitionReader.getRegistry().entrySet()) {
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
