package io.gamioo.sandbox;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

/**
 * JMH测试16：测试线程安全的几个Map的读写性能
 *
 * @author Allen Jiang
 */
@Fork(1)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Group)
public class MapBenchMark {

    @Param({"ConcurrentHashMap", "ConcurrentSkipListMap", "Hashtable", "Collections.synchronizedMap"})
    private String type;

    private Map<Integer, Integer> map;

    @Setup
    public void setUp() {
        switch (type) {
            case "ConcurrentHashMap":
                this.map = new ConcurrentHashMap<>();
                break;
            case "ConcurrentSkipListMap":
                this.map = new ConcurrentSkipListMap<>();
                break;
            case "Hashtable":
                this.map = new Hashtable<>();
                break;
            case "Collections.synchronizedMap":
                this.map = Collections.synchronizedMap(
                        new HashMap<>());
                break;
            default:
                throw new IllegalArgumentException("Illegal map type.");
        }
    }

    @Group("map")
    @GroupThreads(5)
    @Benchmark
    public void putMap() {
        int random = randomIntValue();
        this.map.put(random, random);
    }

    @Group("map")
    @GroupThreads(5)
    @Benchmark
    public Integer getMap() {
        return this.map.get(randomIntValue());
    }

    /**
     * 计算一个随机值用作Map中的Key和Value
     *
     * @return
     */
    private int randomIntValue() {
        return (int) Math.ceil(Math.random() * 600000);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MapBenchMark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
