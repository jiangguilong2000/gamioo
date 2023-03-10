package io.gamioo.sandbox;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * JMH测试15：官方Asymmetric样例
 *
 * @author Allen Jiang
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@State(Scope.Group)
public class AsymmetricBenchMark {
    private AtomicLong counter;

    @Setup
    public void up() {
        counter = new AtomicLong();
    }

    @Benchmark
    @Group("atomic")
    @GroupThreads(3)
    public long inc() {
        return counter.incrementAndGet();
    }

    @Benchmark
    @Group("atomic")
    @GroupThreads(1)
    public long get() {
        return counter.get();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(AsymmetricBenchMark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
