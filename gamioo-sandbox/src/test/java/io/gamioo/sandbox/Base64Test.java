package io.gamioo.sandbox;

import com.github.woostju.ansible.AnsibleClient;
import com.github.woostju.ansible.ReturnValue;
import com.github.woostju.ansible.command.PingCommand;
import com.github.woostju.ssh.SshClientConfig;
import com.github.woostju.ssh.pool.SshClientsPool;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.jupiter.api.*;

import java.util.Base64;
import java.util.Map;

@DisplayName("crypto test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Base64Test {
    private static final Logger logger = LogManager.getLogger(Base64Test.class);


    @BeforeAll
    public static void beforeAll() {


    }

    @AfterAll
    public static void afterAll() {

    }

    @DisplayName("base64")
    @Test
    @Order(1)
    public void base64() {
        String ret= Base64.getEncoder().encodeToString("api_user:api_password".getBytes());
        logger.debug("result:{}", ret);
        Assertions.assertEquals("YXBpX3VzZXI6YXBpX3Bhc3N3b3Jk", ret);
    }


}
