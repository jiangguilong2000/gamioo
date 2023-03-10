package io.gamioo.sandbox;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * JMH测试15：Asymmetric Benchmark样例
 *
 * @author Allen Jiang
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Group)
public class SymmetricBenchmark {
    private AtomicLong counter;

    @Setup
    public void init() {
        this.counter = new AtomicLong();
    }

    @GroupThreads(5)
    @Group("atomic")
    @Benchmark
    public void inc() {
        this.counter.incrementAndGet();
    }

    @GroupThreads(5)
    @Group("atomic")
    @Benchmark
    public long get() {
        return this.counter.get();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SymmetricBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
