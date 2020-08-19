package io.gamioo.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.cache.CacheBuilder;

import io.gamioo.benchmark.Benchmark;

@DisplayName("第三方缓存测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CacheGroupTest {
	private static final Logger logger = LogManager.getLogger(CacheGroupTest.class);
	private static com.google.common.cache.Cache<Integer, String> guavaCache;
	private static com.github.benmanes.caffeine.cache.Cache<Integer, String> caffeineCache;
	private static List<String> array = new ArrayList<>();
	private static int size = 100000;
	private final Benchmark benchmark = new Benchmark(0,size);

	@BeforeAll
	public static void beforeAll() throws Exception {
		guavaCache = CacheBuilder.newBuilder().build();
		caffeineCache = Caffeine.newBuilder().build();

		for (int i = 0; i < size; i++) {
			array.add(String.valueOf(i));
		}

	}

	@BeforeEach
	public void beforeEach() throws Exception {
	}

	@AfterEach
	public void afterEach() throws Exception {

	}

	@Test
	@DisplayName("guava 存入")
	@Order(1)
//	@Execution(ExecutionMode.CONCURRENT)
	public void guavaPut() throws Exception {
		AtomicInteger index = new AtomicInteger(0);
		benchmark.run(8, size, "guava put", () -> {
			int id = index.getAndIncrement();
			guavaCache.put(id, array.get(id));

		});
	}

	@Test
	@DisplayName("guava 取出")
	@Order(2)
//	@Execution(ExecutionMode.CONCURRENT)
	public void guavaGet() throws Exception {
		AtomicInteger index = new AtomicInteger(0);
		benchmark.run(8, size, "guava get", () -> {
		guavaCache.getIfPresent(index.getAndIncrement());
		});
	}

	@Test
	@DisplayName("caffeine 存入")
	@Order(3)
//	@Execution(ExecutionMode.CONCURRENT)
	public void caffeinePut() throws Exception {
		benchmark.run(8, size, "caffeine put", () -> {
			AtomicInteger index = new AtomicInteger(0);
			int id = index.getAndIncrement();
			caffeineCache.put(id, array.get(id));
		});
	}

	@Test
	@DisplayName("caffeine 取出")
	@Order(4)
//	@Execution(ExecutionMode.CONCURRENT)
	public void caffeineGet() throws Exception {
		AtomicInteger index = new AtomicInteger(0);
		benchmark.run(8, size, "caffeine get", () -> {
		caffeineCache.getIfPresent(index.getAndIncrement());
		});
	}

	@AfterAll
	public static void afterAll() throws Exception {

	}
}
