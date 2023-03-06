package io.gamioo.compress;


import com.github.luben.zstd.Zstd;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * 压缩算法的性能测试
 *
 * @author Allen Jiang
 */
@State(Scope.Benchmark)
@Fork(value = 1)
public class CompressBenchMark {
    private static final Logger logger = LogManager.getLogger(CompressBenchMark.class);
    private byte[] array;
    private byte[] compressArray;


    @Setup(Level.Trial)
    public void init() {
        try {
            // 获取URL
            URL url = CompressBenchMark.class.getClassLoader().getResource("message.txt");
            // 通过url获取File的绝对路径
            if (url != null) {
                File file = new File(url.getFile());
                array = FileUtils.readFileToByteArray(file);
                logger.info("data size={}", array.length);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(iterations = 5, time = 2)
    @Measurement(iterations = 10, time = 2)
    public void zstandardCompress() {
        if (compressArray == null) {
            compressArray = Zstd.compress(array);
        } else {
            Zstd.compress(array);
        }


    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(iterations = 5, time = 2)
    @Measurement(iterations = 10, time = 2)
    public void zstandardDecompress() {
        if (compressArray != null) {
            int size = (int) Zstd.decompressedSize(compressArray);
            array = new byte[size];
            Zstd.decompress(array, compressArray);
        }
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(iterations = 5, time = 2)
    @Measurement(iterations = 10, time = 2)
    public void zlibCompress() {
        compressArray = ZlibUtil.compress(array);
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(iterations = 5, time = 2)
    @Measurement(iterations = 10, time = 2)
    public void zlibDecompress() {
        if (compressArray != null) {
            array = ZlibUtil.uncompress(compressArray);
        }
    }


    public static void main(String[] args) {
        Options opt = new OptionsBuilder()
                .include(CompressBenchMark.class.getSimpleName())
                .build();
        try {
            new Runner(opt).run();
        } catch (RunnerException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
