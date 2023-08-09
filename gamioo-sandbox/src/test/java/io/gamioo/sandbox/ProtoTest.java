package io.gamioo.sandbox;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.carrotsearch.sizeof.RamUsageEstimator;
import com.github.houbb.data.factory.core.util.DataUtil;
import com.github.houbb.heaven.util.lang.MathUtil;
import io.fury.Fury;
import io.fury.Language;
import io.fury.ThreadLocalFury;
import io.fury.resolver.MetaContext;
import io.gamioo.common.util.FileUtils;
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
        try {
            byte[]  array = FileUtils.getByteArrayFromFile("message.txt");
            skillFire_s2C_msg= JSON.parseObject(array,SkillFire_S2C_Msg.class);
            logger.info(skillFire_s2C_msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

       // skillFire_s2C_msg= DataUtil.build(SkillFire_S2C_Msg.class);
      //  logger.info(JSON.toJSONString(skillFire_s2C_msg));
         fury = Fury.builder().withLanguage(Language.JAVA)
                .withRefTracking(true).requireClassRegistration(false).withNumberCompressed(false).build();
        long size = RamUsageEstimator.sizeOf(skillFire_s2C_msg);
        logger. info(size);
    }

    @AfterAll
    public static void afterAll() {

    }

    @DisplayName("Json2 Serializable")
    @Test
    @Order(1)
    public void handleJson2Serialize() {
        bytes = JSONB.toBytes(skillFire_s2C_msg);
        long size = RamUsageEstimator.sizeOf(skillFire_s2C_msg);
        //   logger. info(bytes.length);
        // String size2 = RamUsageEstimator.humanReadableUnits(size);
        logger. info( "Json2 Serializable "+MathUtils.prettyPercentage((double)bytes.length/size));
    }

    @DisplayName("Json2 Serializable with BeanToArray")
    @Test
    @Order(2)
    public void handleJson2WithBeanToArraySerialize() {
        bytes = JSONB.toBytes(skillFire_s2C_msg, JSONWriter.Feature.BeanToArray);
        long size = RamUsageEstimator.sizeOf(skillFire_s2C_msg);
        //   logger. info(bytes.length);
        // String size2 = RamUsageEstimator.humanReadableUnits(size);
        logger. info( "Json2 Serializable  with BeanToArray "+MathUtils.prettyPercentage((double)bytes.length/size));
    }
    @DisplayName("Json2 Serializable with beanToArray and fieldBased")
    @Test
    @Order(2)
    public void handleJson2WithBeanToArrayAndFieldBasedSerialize() {
        bytes = JSONB.toBytes(skillFire_s2C_msg, JSONWriter.Feature.BeanToArray, JSONWriter.Feature.FieldBased);
        long size = RamUsageEstimator.sizeOf(skillFire_s2C_msg);
        //   logger. info(bytes.length);
        // String size2 = RamUsageEstimator.humanReadableUnits(size);
        logger. info( "Json2 Serializable  with beanToArray and fieldBased "+MathUtils.prettyPercentage((double)bytes.length/size));
    }



//    @DisplayName("ThreadLocalFury Serializable")
//    @Test
//    @Order(3)
//    public void handleThreadLocalFurySerialize() {
//        ThreadLocalFury  fury = Fury.builder().withLanguage(Language.JAVA)
//                .withRefTracking(true).requireClassRegistration(false).withNumberCompressed(false).buildThreadLocalFury();
//       bytes = fury.serialize(skillFire_s2C_msg);
//        long size = RamUsageEstimator.sizeOf(skillFire_s2C_msg);
//     //   logger. info(bytes.length);
//     // String size2 = RamUsageEstimator.humanReadableUnits(size);
//     logger. info( "Fury Serializable "+MathUtils.prettyPercentage((double)bytes.length/size));
//    }
//
//    @DisplayName("Fury Deserialize")
//    @Order(8)
//    public void handleThreadLocalFuryDeserialize() {
//        ThreadLocalFury  fury2 = Fury.builder().withLanguage(Language.JAVA)
//                .withRefTracking(true).requireClassRegistration(false).withNumberCompressed(false).buildThreadLocalFury();
//
//        logger. info( fury.deserializeJavaObject(bytes,SkillFire_S2C_Msg.class));
//
//        logger. info( fury.deserializeJavaObject(bytes,SkillFire_S2C_Msg.class));
//    }

    @DisplayName("Fury Serializable")
    @Test
    @Order(3)
    public void handleFurySerialize() {
        bytes = fury.serializeJavaObject(skillFire_s2C_msg);

        long size = RamUsageEstimator.sizeOf(skillFire_s2C_msg);
        //   logger. info(bytes.length);
        // String size2 = RamUsageEstimator.humanReadableUnits(size);
        logger. info( "Fury Serializable "+MathUtils.prettyPercentage((double)bytes.length/size));
    }

    @DisplayName("Fury Serializable with Number Compress")
    @Test
    @Order(4)
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
    @Order(5)
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
    @Order(6)
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
    @Order(7)
    public void handleFurySerializeWithCompressAndRegister() {
//.withDeserializeUnExistClassEnabled(true)
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
    @Order(8)
    public void handleFuryDeserialize() {
       // logger. info( fury.deserializeJavaObject(bytes,SkillFire_S2C_Msg.class));
    }

    @DisplayName("Protostuff Serializable")
    @Test
    @Order(100)
    public void handleProtostuffSerialize() {
        bytes = SerializingUtil.serialize(skillFire_s2C_msg);
        long size = RamUsageEstimator.sizeOf(skillFire_s2C_msg);
      //  logger.info(bytes.length);
        logger.info("Protostuff Serializable "+MathUtils.prettyPercentage((double)bytes.length/size));
    }

    @DisplayName("Protostuff Deserialize")
    @Test
    @Disabled
    @Order(101)
    public void handleProtostuffDeserialize() {
        logger. info(SerializingUtil.deserialize(bytes, SkillFire_S2C_Msg.class));
    }


}
