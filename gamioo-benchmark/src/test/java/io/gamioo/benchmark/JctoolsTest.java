package io.gamioo.benchmark;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jctools.maps.NonBlockingHashMap;
import org.jctools.maps.NonBlockingHashMapLong;
import org.junit.jupiter.api.*;

import java.time.Instant;

@DisplayName("jctools好玩的东西测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JctoolsTest {
    private static final Logger logger = LogManager.getLogger(JctoolsTest.class);
    // private static Cache<Integer,String> guavaCache;
    private static Instant startTime;

    @BeforeAll
    public static void beforeAll() throws Exception {
        // guavaCache = CacheBuilder.newBuilder().build();
        startTime = Instant.now();

    }

    @BeforeEach
    public void beforeEach() throws Exception {
        // System.out.printf("array size={},compress array={}",
        // array.length,compressArray.length);
    }

    @AfterEach
    public void afterEach() throws Exception {

    }


    @Test
    @DisplayName("NonBlockingHashMap")
//    @RepeatedTest(10000)
//    @Execution(ExecutionMode.CONCURRENT)
    public void NonBlockingHashMapTest() throws Exception {
        //NonBlockingHashMap 是对 ConcurrentHashMap 的增强，对多 CPU 的支持以及高并发更新提供更好的性能
        NonBlockingHashMap<String,String> map=new NonBlockingHashMap();
        map.put("key","value");
        map.get("key");
        map.values();

    }

    @Test
    @DisplayName("NonBlockingHashMapLong 取出")
    // @Execution(ExecutionMode.SAME_THREAD)
    public void NonBlockingHashMapLongTest() throws Exception {
        NonBlockingHashMapLong<String> map=new NonBlockingHashMapLong();
        map.put(123456767899L,"value");
       String value=map.get(123456767899L);
        map.values();
    }

    @AfterAll
    public static void afterAll() throws Exception {

    }
}