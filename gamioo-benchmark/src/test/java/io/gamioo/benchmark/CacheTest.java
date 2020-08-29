package io.gamioo.benchmark;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.time.Duration;
import java.time.Instant;

@DisplayName("第三方缓存测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CacheTest {
	private static final Logger logger = LogManager.getLogger(CacheTest.class);
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

//	@ParameterizedTest
//	@ValueSource(strings = {"one", "two", "three"})
//	@DisplayName("参数化测试")
//	public void parameterizedTest(String string) {
//	    System.out.println(string);
//	    Assertions.assertTrue(StringUtils.isNotBlank(string));
//	}

	@Test
	@DisplayName("Guava 存入")
	@RepeatedTest(10000)
	@Execution(ExecutionMode.CONCURRENT)
	public void guavaPut() throws Exception {
		int index = RandomUtils.nextInt(0, 10000);
		// guavaCache.put(index,"Hello Guava Cache");
//		benchmark.run("guava  function test", () -> {
//
//		});
		// System.out.println(this.getClass() + "-guavaPut-" +
		// Thread.currentThread().getName());
	}

	@Test
	@DisplayName("Guava 取出")
	@RepeatedTest(10000)
	@Execution(ExecutionMode.CONCURRENT)
	// @Execution(ExecutionMode.SAME_THREAD)
	public void guavaGet() throws Exception {
		int index = RandomUtils.nextInt(0, 10000);
		// guavaCache.getIfPresent(index);
//	System.out.println("-guavaGet-" + Thread.currentThread().getName());
	}

	@AfterAll
	public static void afterAll() throws Exception {
		Instant endTime = Instant.now();
		long interval = Duration.between(startTime, endTime).toMillis();
		logger.debug("total= {0} ms,times={1}, speed= {2} ms", interval, 10000, interval * 1f / 10000);
	}
}
