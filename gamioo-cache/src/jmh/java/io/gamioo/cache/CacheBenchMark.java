package io.gamioo.cache;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.RandomUtils;
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
@State(Scope.Group)
@BenchmarkMode({Mode.Throughput})
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1)
public class CacheBenchMark {
    @Param({"guava", "caffeine"})
    private String type;

    private com.google.common.cache.Cache<Integer, Integer> guavaCache;
    private com.github.benmanes.caffeine.cache.Cache<Integer, Integer> caffeineCache;

    private int key;


    @Setup(Level.Trial)
    public void init() {
        switch (type) {
            case "guava":
                this.guavaCache = CacheBuilder.newBuilder().build();
                break;
            case "caffeine":
                this.caffeineCache = Caffeine.newBuilder().build();
                break;
            default:
                throw new IllegalArgumentException("Illegal cache type.");
        }

    }

    @TearDown(Level.Trial)
    public void destroy() {
        this.guavaCache = null;
        this.caffeineCache = null;
    }

    @Group("cache")
    @GroupThreads()
    @Setup(Level.Invocation)
    public void prepare() {
        key = RandomUtils.nextInt(0, Integer.MAX_VALUE);
    }


    @Benchmark
    @Group("cache")
    @GroupThreads(5)
    public void put() {
        if (guavaCache != null) {
            guavaCache.put(key, key);
        }
        if (caffeineCache != null) {
            caffeineCache.put(key, key);
        }
    }

    @Benchmark
    @Group("cache")
    @GroupThreads(5)
    public Integer get() {
        if (guavaCache != null) {
            return guavaCache.getIfPresent(key);
        }
        if (caffeineCache != null) {
            return caffeineCache.getIfPresent(key);
        }
        return 0;
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
