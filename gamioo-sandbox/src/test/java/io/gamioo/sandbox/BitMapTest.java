package io.gamioo.sandbox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.BitSet;

@DisplayName("BitMap test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BitMapTest {
    private static final Logger logger = LogManager.getLogger(BitMapTest.class);


    @BeforeAll
    public static void beforeAll() throws IOException {
    }

    @AfterAll
    public static void afterAll() {

    }

    @DisplayName("bitmap")
    @Test
    @Order(2)
    public void nativeFind() throws Exception {
        BitSet bit = new BitSet();
        BitSet other = new BitSet();
        logger.debug("size={}", bit.size());
        int a[] = {2, 3, 14, 7, 0, 66};

        //赋值
        for (int num : a) {
//进入某层
            bit.set(num);

        }

        //离开某层
        //  bit.clear(2);

        int b[] = {1, 4};
        for (int num : b) {
            other.set(num, true);
        }

        logger.debug("size={}", bit.size());
        //排序

        for (int i = 0; i < bit.size(); i++) {

            if (bit.get(i)) {
                logger.debug(i);
            }
        }
        if (bit.intersects(other)) {
            logger.debug("same layer");
        }
        logger.debug("{}", bit.toByteArray());
        logger.debug("{}", bit.toLongArray());
        logger.debug("{}", bit.toString());
     

        logger.debug("end");
    }


}
