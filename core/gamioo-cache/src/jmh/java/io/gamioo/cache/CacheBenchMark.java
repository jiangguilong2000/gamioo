package io.gamioo.cache;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 缓存测试
 *
 * @author Allen Jiang
 */
@State(Scope.Benchmark)
@Fork(value = 1)
public class CacheBenchMark {
    private static final Logger logger = LogManager.getLogger(CacheBenchMark.class);
    private com.google.common.cache.Cache<Integer, Integer> guavaCache;
    private com.github.benmanes.caffeine.cache.Cache<Integer, Integer> caffeineCache;

    private int key;


    @Setup(Level.Trial)
    public void init() {
        guavaCache = CacheBuilder.newBuilder().build();
        caffeineCache = Caffeine.newBuilder().build();
    }

    @Setup(Level.Invocation)
    public void prepare() {
        key = RandomUtils.nextInt(0, Integer.MAX_VALUE);
    }


    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(iterations = 5, time = 2)
    @Measurement(iterations = 10, time = 2)
    public void guavaPut() {
        guavaCache.put(key, key);

    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(iterations = 5, time = 2)
    @Measurement(iterations = 10, time = 2)
    public void guavaGet() {
        guavaCache.getIfPresent(key);
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(iterations = 5, time = 2)
    @Measurement(iterations = 10, time = 2)
    public void caffeinePut() {
        caffeineCache.put(key, key);
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(iterations = 5, time = 2)
    @Measurement(iterations = 10, time = 2)
    public void caffeineGet() {
        caffeineCache.getIfPresent(key);
    }


    public static void main(String[] args) {
        Options opt = new OptionsBuilder()
                .include(CacheBenchMark.class.getSimpleName())
                .build();

        try {
            new Runner(opt).run();
        } catch (RunnerException e) {
            e.printStackTrace();
        }
    }
}
