package io.gamioo.sandbox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;

@DisplayName("crypto test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CryptoTest {
    private static final Logger logger = LogManager.getLogger(CryptoTest.class);


    @BeforeAll
    public static void beforeAll() throws IOException {
    }

    @AfterAll
    public static void afterAll() {

    }

    @DisplayName("bCrypt")
    @Test
    @Order(1)
    public void bCrypt() {
        String password = "neil";
        String encodedPwd = "$apr1$PjLB3DLO$J1zN2Bbit2A9e8FQdALjb0";
        //密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        //加密
        String newPassword = passwordEncoder.encode(password);
        logger.debug("raw pwd:{},encoded pwd:{}", password, newPassword);

        //对比这两个密码是否是同一个密码
        logger.debug("compare {},{}", password, encodedPwd);
        boolean matches = passwordEncoder.matches(password, encodedPwd);
        if (matches) {
            logger.debug("match");
        } else {
            logger.debug("diff");
        }

    }


}
