package io.gamioo.sandbox;

import com.carrotsearch.sizeof.RamUsageEstimator;
import com.github.houbb.data.factory.core.util.DataUtil;
import com.github.houbb.heaven.util.lang.MathUtil;
import io.fury.Fury;
import io.fury.Language;
import io.fury.resolver.MetaContext;
import io.gamioo.common.util.MathUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.io.IOException;

@DisplayName("proto test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProtoTest {
    private static final Logger logger = LogManager.getLogger(ProtoTest.class);


    private static SkillFire_S2C_Msg skillFire_s2C_msg;

    private static  Fury fury;

    private static byte[] bytes;
    @BeforeAll
    public static void beforeAll() throws IOException {
        skillFire_s2C_msg= DataUtil.build(SkillFire_S2C_Msg.class);
        logger.info(skillFire_s2C_msg);
         fury = Fury.builder().withLanguage(Language.JAVA)
                .withRefTracking(true).requireClassRegistration(false).withNumberCompressed(false).build();
        long size = RamUsageEstimator.sizeOf(skillFire_s2C_msg);
        logger. info(size);
    }

    @AfterAll
    public static void afterAll() {

    }

    @DisplayName("Fury Serializable")
    @Test
    @Order(1)
    public void handleFurySerialize() {
       bytes = fury.serializeJavaObject(skillFire_s2C_msg);
        long size = RamUsageEstimator.sizeOf(skillFire_s2C_msg);
     //   logger. info(bytes.length);
     // String size2 = RamUsageEstimator.humanReadableUnits(size);
     logger. info( "Fury Serializable "+MathUtils.prettyPercentage((double)bytes.length/size));
    }

    @DisplayName("Fury Serializable with Number Compress")
    @Test
    @Order(2)
    public void handleFurySerializeWithCompress() {
        fury = Fury.builder().withLanguage(Language.JAVA)
                .withRefTracking(true).requireClassRegistration(false).withNumberCompressed(true).build();

        bytes = fury.serializeJavaObject(skillFire_s2C_msg);
        long size = RamUsageEstimator.sizeOf(skillFire_s2C_msg);
      //  logger. info(bytes.length);
        // String size2 = RamUsageEstimator.humanReadableUnits(size);
        logger. info("Fury Serializable with Number Compress "+MathUtils.prettyPercentage((double)bytes.length/size));
    }

    @DisplayName("Fury Serializable with Number Compress and RefTracking close")
    @Test
    @Order(3)
    public void handleFurySerializeWithCompressAndRefTrackingClose() {

        fury = Fury.builder().withLanguage(Language.JAVA)
                .withRefTracking(false).requireClassRegistration(false).withNumberCompressed(true).build();
        bytes = fury.serializeJavaObject(skillFire_s2C_msg);
        long size = RamUsageEstimator.sizeOf(skillFire_s2C_msg);
     //   logger. info(bytes.length);
        // String size2 = RamUsageEstimator.humanReadableUnits(size);
        logger. info("Fury Serializable with Number Compress and RefTracking close "+ MathUtils.prettyPercentage((double)bytes.length/size));
    }

    @DisplayName("Fury Serializable with Number Compress and RefTracking close and class register")
    @Test
    @Order(3)
    public void handleFurySerializeWithCompressAndRefTrackingCloseAndRegister() {

        fury = Fury.builder().withLanguage(Language.JAVA)
                .withRefTracking(false).requireClassRegistration(true).withNumberCompressed(true).build();
        fury.register(SkillFire_S2C_Msg.class);
        fury.register(SkillCategory.class);
        fury.register(HarmDTO.class);
        bytes = fury.serializeJavaObject(skillFire_s2C_msg);
        long size = RamUsageEstimator.sizeOf(skillFire_s2C_msg);
      //  logger. info(bytes.length);
        // String size2 = RamUsageEstimator.humanReadableUnits(size);
        logger. info("Fury Serializable with Number Compress and RefTracking close and class register "+MathUtils.prettyPercentage((double)bytes.length/size));
    }

    @DisplayName("Fury Serializable with Number Compress and class register")
    @Test
    @Order(3)
    public void handleFurySerializeWithCompressAndRegister() {

        fury = Fury.builder().withLanguage(Language.JAVA)
                .withRefTracking(true).requireClassRegistration(true).withNumberCompressed(true).build();
        fury.register(SkillFire_S2C_Msg.class);
        fury.register(SkillCategory.class);
        fury.register(HarmDTO.class);
        bytes = fury.serializeJavaObject(skillFire_s2C_msg);
        long size = RamUsageEstimator.sizeOf(skillFire_s2C_msg);
     //   logger. info(bytes.length);
        // String size2 = RamUsageEstimator.humanReadableUnits(size);
        logger. info("Fury Serializable with Number Compress and class register "+MathUtils.prettyPercentage((double)bytes.length/size));
    }
    @DisplayName("Fury Deserialize")
    @Test
    @Disabled
    @Order(8)
    public void handleFuryDeserialize() {
        logger. info( fury.deserializeJavaObject(bytes,SkillFire_S2C_Msg.class));
    }

    @DisplayName("Protostuff Serializable")
    @Test
    @Order(5)
    public void handleProtostuffSerialize() {
        bytes = SerializingUtil.serialize(skillFire_s2C_msg);
        long size = RamUsageEstimator.sizeOf(skillFire_s2C_msg);
      //  logger.info(bytes.length);
        logger.info("Protostuff Serializable "+MathUtils.prettyPercentage((double)bytes.length/size));
    }

    @DisplayName("Protostuff Deserialize")
    @Test
    @Disabled
    @Order(6)
    public void handleProtostuffDeserialize() {
        logger. info(SerializingUtil.deserialize(bytes, SkillFire_S2C_Msg.class));
    }


}
