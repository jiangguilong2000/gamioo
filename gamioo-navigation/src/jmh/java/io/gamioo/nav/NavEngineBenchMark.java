package io.gamioo.nav;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Allen Jiang
 */
@State(Scope.Benchmark)
@BenchmarkMode({Mode.Throughput})
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1)
public class NavEngineBenchMark {
    /**
     * Benchmark                Mode  Cnt       Score      Error  Units
     * Nav3DBenchMark.find     thrpt   10  248280.038 ± 1460.364  ops/s
     * Nav3DBenchMark.raycast  thrpt   10  816733.804 ± 9337.084  ops/s
     */
    private NavEngine nav;
    public static int id;
    float[] src = new float[]{-112.082f, 0.359222f, 55.5665f};
    float[] end = new float[]{-49.0154f, 0.0922241f, 104.259f};

    private float[] extents = {1.f, 1.f, 1.f};

    float[] target = new float[]{-86.6f, 1.8f, 53.6f};
    private String navFilePath = "solo_navmesh.bin";

    private Easy3dNav easyNav = new Easy3dNav();


    @Setup(Level.Trial)
    public void init() throws IOException {
        nav = new NavEngine();
        id = 1;
        nav.init(id, navFilePath);
        //初始化寻路对象
        easyNav = new Easy3dNav();
        //默认为true，可以忽略
        easyNav.setUseU3dData(false);
        //默认为false，查看需要设置为true
        easyNav.setPrintMeshInfo(false);
        easyNav.setExtents(extents);
        easyNav.init(navFilePath);
    }


    @Benchmark
    public List<float[]> nativeFind() {
        return nav.find(src, end);
    }

    @Benchmark
    public float[] nativeRaycast() {
        return nav.raycast(src, end);
    }

    @Benchmark
    public float[] nativeFindNearest() {
        return nav.findNearest(target);
    }

    @Benchmark
    public List<float[]> javaFind() {
        return easyNav.find(src, end);
    }

    @Benchmark
    public float[] javaRaycast() {
        return easyNav.raycast(src, end);
    }

    @Benchmark
    public float[] javaFindNearest() {
        return easyNav.findNearest(target);
    }


    public static void main(String[] args) {
        Options opt = new OptionsBuilder()
                .include(NavEngineBenchMark.class.getSimpleName()).build();

        try {
            new Runner(opt).run();
        } catch (RunnerException e) {
            e.printStackTrace();
        }
    }
}
