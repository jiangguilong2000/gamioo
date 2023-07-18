package io.gamioo.compress;


import info.debatty.java.stringsimilarity.Jaccard;
import info.debatty.java.stringsimilarity.JaroWinkler;
import info.debatty.java.stringsimilarity.RatcliffObershelp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 压缩算法的性能测试
 *
 * @author Allen Jiang
 */
@State(Scope.Benchmark)
@Fork(value = 1)
public class SimilarityBenchMark {
    private static final Logger logger = LogManager.getLogger(SimilarityBenchMark.class);
    private String str1;
    private String str2;
    private JaroWinkler jaroWinkler;
    private Jaccard jaccard;

    private RatcliffObershelp ratcliffObershelp;

    @Setup(Level.Trial)
    public void init() {
        jaroWinkler = new JaroWinkler();
        jaccard=new Jaccard();
        ratcliffObershelp=new RatcliffObershelp();
        str1 = "LuaException: Util/StrUtil.lua:222: attempt to compare nil with number";
        str2 = "LuaException: c# exception:XLua.LuaException: Util/StrUtil.lua:222: attempt to compare nil with number";
    }


    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(iterations = 5, time = 2)
    @Measurement(iterations = 10, time = 2)
    public double handleJaroWinkler() {
        return jaroWinkler.similarity(str1, str2);
    }


    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(iterations = 5, time = 2)
    @Measurement(iterations = 10, time = 2)
    public double handleJaccard() {
        return jaccard.similarity(str1, str2);
    }
    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(iterations = 5, time = 2)
    @Measurement(iterations = 10, time = 2)
    public double handleRatcliffObershelp() {
        return ratcliffObershelp.similarity(str1, str2);
    }


    public static void main(String[] args) {
        Options opt = new OptionsBuilder()
                .include(SimilarityBenchMark.class.getSimpleName())
                .build();
        try {
            new Runner(opt).run();
        } catch (RunnerException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
