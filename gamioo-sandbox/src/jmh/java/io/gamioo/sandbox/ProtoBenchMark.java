package io.gamioo.sandbox;

import com.carrotsearch.sizeof.RamUsageEstimator;
import com.github.houbb.data.factory.core.util.DataUtil;
import io.fury.Fury;
import io.fury.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Allen Jiang
 */
@Fork(1)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class ProtoBenchMark {
    private static final Logger logger = LogManager.getLogger(ProtoBenchMark.class);
    private SkillFire_S2C_Msg skillFire_s2C_msg;
    private Fury fury;

    private Fury furyX;

    private byte[] furyArray;

    private byte[] furyArrayX;

    private byte[] protoArray;


    @Setup
    public void init() {
        skillFire_s2C_msg = DataUtil.build(SkillFire_S2C_Msg.class);
        fury = Fury.builder().withLanguage(Language.JAVA)
                .withRefTracking(true).requireClassRegistration(false).build();

        furyX = Fury.builder().withLanguage(Language.JAVA)
                .withRefTracking(false).requireClassRegistration(true).withNumberCompressed(true).build();
        furyX.register(SkillFire_S2C_Msg.class);
        furyX.register(SkillCategory.class);
        furyX.register(HarmDTO.class);


        furyArray = fury.serializeJavaObject(skillFire_s2C_msg);
        furyArrayX=furyX.serializeJavaObject(skillFire_s2C_msg);
        protoArray = SerializingUtil.serialize(skillFire_s2C_msg);
        String size = RamUsageEstimator.humanReadableUnits(RamUsageEstimator.sizeOf(skillFire_s2C_msg));
        logger.debug("size:{}", size);
    }

    // @Group("fury")
    @Benchmark
    public byte[] furySerialize() {
        return fury.serialize(skillFire_s2C_msg);
    }

    //@Group("fury")
    @Benchmark
    public SkillFire_S2C_Msg furyDeserialize() {
        return fury.deserializeJavaObject(furyArray, SkillFire_S2C_Msg.class);
    }

    @Benchmark
    public byte[] furySerializeEnhance() {
        return furyX.serialize(skillFire_s2C_msg);
    }

    //@Group("fury")
    @Benchmark
    public SkillFire_S2C_Msg furyDeserializeEnhance() {
        return furyX.deserializeJavaObject(furyArrayX, SkillFire_S2C_Msg.class);
    }

    //  @Group("protostuff")
    @Benchmark
    public byte[] protostuffSerialize() {
        return SerializingUtil.serialize(skillFire_s2C_msg);
    }

    //  @Group("protostuff")
    @Benchmark
    public SkillFire_S2C_Msg protostuffDeserialize() {
        return SerializingUtil.deserialize(protoArray, SkillFire_S2C_Msg.class);
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ProtoBenchMark.class.getSimpleName()).result("result.json")
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(opt).run();
    }
}
