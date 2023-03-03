package io.gamioo.game;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;

@DisplayName("Rehash测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BalanceBusinessExecutorTest {
    private static final Logger logger = LogManager.getLogger(BalanceBusinessExecutorTest.class);
    private static List<Long> list;

    public static int DELAY = 10_000;

    @BeforeAll
    public static void beforeAll() throws Exception {
        list = new ArrayList<>();
        for (long i = 0; i < 2000; i++) {
            long value = RandomUtils.nextLong(1000000, 9999999999l);
            list.add(value);
        }


    }

    @BeforeEach
    public void beforeEach() throws Exception {

    }

    @AfterEach
    public void afterEach() throws Exception {

    }

    @DisplayName("jctools hash运算")
    @Test
    public void jctoolsHash() throws Exception {
        Map<Integer, DeviationDTO> store = new HashMap<>();
        for (Long id : list) {
            int h = System.identityHashCode(id);
            ;
            int index = (int) (h & 7);
            DeviationDTO dto = store.get(index);
            if (dto == null) {
                dto = new DeviationDTO();
                dto.setValue(1);
                store.put(index, dto);
            } else {
                dto.increase();
            }

        }
        for (DeviationDTO e : store.values()) {
            e.setRatio(String.format("%.4f", Math.abs(0.125f - e.getValue() / 2000f) * 100) + "%");
        }

        logger.debug("rehash8 store={}", store);
    }


    @DisplayName("取模运算")
    @Test
    public void modulus() throws Exception {
        Map<Integer, DeviationDTO> store = new HashMap<>();
        for (long id : list) {
            int index = (int) (id & 7);
            DeviationDTO dto = store.get(index);
            if (dto == null) {
                dto = new DeviationDTO();
                dto.setValue(1);
                store.put(index, dto);
            } else {
                dto.increase();
            }
        }

        for (DeviationDTO e : store.values()) {
            e.setRatio(String.format("%.4f", Math.abs(0.125f - e.getValue() / 2000f) * 100) + "%");
        }

        logger.debug("modulus store={}", store);

    }

    @DisplayName("rehash8取模运算")
    @Test
    public void rehash8() throws Exception {
        Map<Integer, DeviationDTO> store = new HashMap<>();
        for (Long id : list) {
            int h = id.hashCode();
            h ^= h >>> 16;
            int index = (int) (h & 7);
            DeviationDTO dto = store.get(index);
            if (dto == null) {
                dto = new DeviationDTO();
                dto.setValue(1);
                store.put(index, dto);
            } else {
                dto.increase();
            }

        }
        for (DeviationDTO e : store.values()) {
            e.setRatio(String.format("%.4f", Math.abs(0.125f - e.getValue() / 2000f) * 100) + "%");
        }

        logger.debug("rehash8 store={}", store);
    }

    @DisplayName("rehash7取模运算")
    @Test
    public void rehash7() throws Exception {
        Map<Integer, DeviationDTO> store = new HashMap<>();
        for (Long id : list) {
            int h = 0;
            h ^= id.hashCode();
            h ^= (h >>> 20) ^ (h >>> 12);
            h = h ^ (h >>> 7) ^ (h >>> 4);
            int index = h & 7;
            DeviationDTO dto = store.get(index);
            if (dto == null) {
                dto = new DeviationDTO();
                dto.setValue(1);
                store.put(index, dto);
            } else {
                dto.increase();
            }
        }

        for (DeviationDTO e : store.values()) {

            e.setRatio(String.format("%.4f", Math.abs(0.125f - e.getValue() / 2000f) * 100) + "%");
        }

        logger.debug("rehash7 store={}", store);
    }

    @DisplayName("协程测试")
    @Test
    public void fibberTest1() throws Exception {
        ExecutorService scheduler = Executors.newFixedThreadPool(8);
        ThreadFactory factory = Thread.ofVirtual().scheduler(scheduler).name("test-fiber-runner").factory();
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Thread thread = factory.newThread(() -> {
                logger.debug("hello world start");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.debug("hello world end");
            });
            thread.start();
            threadList.add(thread);
        }
        for (Thread thread : threadList) {
            thread.join(5000);
        }

        scheduler.shutdown();
        scheduler.awaitTermination(60000, TimeUnit.MILLISECONDS);
    }

    @DisplayName("协程测试2")
    @Test
    public void fibberTest2() throws Exception {
        int NTASKS = 10;
        List<Future<String>> futureList = new LinkedList<>();
        ThreadFactory factory = Thread.ofVirtual().name("test-fiber-runner").factory();
        ExecutorService exec = Executors.newFixedThreadPool(NTASKS, factory);
        for (int i = 1; i <= NTASKS; i++) {
            String taskname = "task-" + i;
            Callable<String> callable = () -> {
                logger.debug("{} start", taskname);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.debug("{} end", taskname);
                return taskname;
            };
            Future<String> future = exec.submit(callable);
            futureList.add(future);
        }

        futureList.forEach(future -> {
            try {
                String result = future.get();
                logger.debug("{}", result);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }


        });

        exec.shutdown();
        exec.awaitTermination(60000, TimeUnit.MILLISECONDS);


    }

}
