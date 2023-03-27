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
        String password = "opm8M6Aw";
        String encodedPwd = "$2y$08$/p3W60znwXe7BLWB19NzXOMnFBO6QqWFj6E4w4XxFJ6TEX1qRPCY.";
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
