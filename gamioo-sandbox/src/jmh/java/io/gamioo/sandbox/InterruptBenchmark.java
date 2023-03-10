package io.gamioo.sandbox;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * JMH测试16：Interrupts Benchmark样例
 *
 * @author Allen Jiang
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Group)
public class InterruptBenchmark {
    private BlockingQueue<Integer> queue;

    private final static int VALUE = Integer.MAX_VALUE;

    @Setup
    public void init() {
        this.queue = new ArrayBlockingQueue<>(10);
    }

    @GroupThreads(5)
    @Group("queue")
    @Warmup(iterations = 5, time = 1)
    @Measurement(iterations = 5, time = 1)
    @Benchmark
    public void put()
            throws InterruptedException {
        this.queue.put(VALUE);
    }

    @GroupThreads(5)
    @Group("queue")
    @Warmup(iterations = 5, time = 1)
    @Measurement(iterations = 5, time = 1)
    @Benchmark
    public int take()
            throws InterruptedException {
        return this.queue.take();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(InterruptBenchmark.class.getSimpleName())
                // 将每个批次的超时时间设置为10秒
                .timeout(TimeValue.milliseconds(10000))
                .build();
        new Runner(opt).run();
    }
}
