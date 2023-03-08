package io.gamioo;

import com.alibaba.fastjson2.JSONObject;
import io.gamioo.common.util.JsonXmlUtil;
import io.gamioo.common.util.XMLUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.junit.jupiter.api.*;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */

@DisplayName("IOC测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JsonXmlUtilTest {
    private static final Logger logger = LogManager.getLogger(JsonXmlUtilTest.class);
    //private final Benchmark benchmark=new Benchmark(10000);
    private static Element root;

    @BeforeAll
    public static void beforeAll() throws Exception {
        Document document = XMLUtil.loadFromFile("gate-config.xml");
        root = document.getRootElement();

    }

    @Test
    @Order(1)
    public void test() throws Exception {
        JSONObject obj = new JSONObject();
        JsonXmlUtil.xml2Json(root, obj);
        logger.debug(obj);

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
