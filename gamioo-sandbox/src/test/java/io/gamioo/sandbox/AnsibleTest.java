package io.gamioo.sandbox;

import com.github.woostju.ansible.AnsibleClient;
import com.github.woostju.ansible.ReturnValue;
import com.github.woostju.ansible.command.PingCommand;
import com.github.woostju.ssh.SshClientConfig;
import com.github.woostju.ssh.pool.SshClientsPool;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.util.Map;

@DisplayName("crypto test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AnsibleTest {
    private static final Logger logger = LogManager.getLogger(AnsibleTest.class);
    private static SshClientsPool pool = new SshClientsPool();
    private static AnsibleClient client;

    @BeforeAll
    public static void beforeAll() {

        client = new AnsibleClient(new SshClientConfig("115.159.87.108", 3737, "root", "", null), pool);

        // client = new AnsibleClient();

    }

    @AfterAll
    public static void afterAll() {

    }

    @DisplayName("ssh")
    @Test
    @Order(1)
    public void ssh() {
        Map<String, ReturnValue> result = client.execute(new PingCommand(Lists.newArrayList("106.53.236.196")), 1000);
        logger.debug("result", result);

       
    }


}
