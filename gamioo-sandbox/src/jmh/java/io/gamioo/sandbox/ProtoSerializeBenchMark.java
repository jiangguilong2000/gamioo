package io.gamioo.sandbox;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.carrotsearch.sizeof.RamUsageEstimator;
import com.github.houbb.data.factory.core.util.DataUtil;
import io.fury.Fury;
import io.fury.Language;
import io.gamioo.common.util.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author Allen Jiang
 */
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class ProtoSerializeBenchMark {
    private static final Logger logger = LogManager.getLogger(ProtoSerializeBenchMark.class);
    private SkillFire_S2C_Msg  skillFire_s2C_msg = DataUtil.build(SkillFire_S2C_Msg.class);
    private Fury fury;

    private Fury furyX;




    @Setup
    public void init() {
        try {
          byte[]  array = FileUtils.getByteArrayFromFile("message.txt");
            skillFire_s2C_msg= JSON.parseObject(array,SkillFire_S2C_Msg.class);
            logger.info(skillFire_s2C_msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        fury = Fury.builder().withLanguage(Language.JAVA)
                .withRefTracking(true).requireClassRegistration(false).build();

        furyX = Fury.builder().withLanguage(Language.JAVA)
                .withRefTracking(false).requireClassRegistration(true).withNumberCompressed(true).build();
        furyX.register(SkillFire_S2C_Msg.class);
        furyX.register(SkillCategory.class);
        furyX.register(HarmDTO.class);



       // String size = RamUsageEstimator.humanReadableUnits(RamUsageEstimator.sizeOf(skillFire_s2C_msg));
        logger.debug("size:{}", RamUsageEstimator.sizeOf(skillFire_s2C_msg));
    }
    @Benchmark
    public byte[] furySerialize() {
        return fury.serialize(skillFire_s2C_msg);
    }
    @Benchmark
    public byte[] furySerializeEnhance() {
        return furyX.serialize(skillFire_s2C_msg);
    }
    @Benchmark
    public byte[] jsonSerialize() {
        return JSONB.toBytes(skillFire_s2C_msg);
    }



    @Benchmark
    public byte[] jsonSerializeWithBeanToArray() {
        return JSONB.toBytes(skillFire_s2C_msg, JSONWriter.Feature.BeanToArray);
    }

    @Benchmark
    public byte[] jsonSerializeWithBeanToArrayAndFieldBase() {
        return JSONB.toBytes(skillFire_s2C_msg, JSONWriter.Feature.BeanToArray,JSONWriter.Feature.FieldBased);
    }
    @Benchmark
    public byte[] protostuffSerialize() {
        return SerializingUtil.serialize(skillFire_s2C_msg);
    }




    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ProtoSerializeBenchMark.class.getSimpleName()).result("result.json")
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(opt).run();
    }
}
