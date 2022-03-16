/*
 * Copyright 2015-2020 Gamioo Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gamioo.ioc;

import io.gamioo.ioc.context.ConfigApplicationContext;
import io.gamioo.ioc.entity.ServerConfig;
import io.gamioo.ioc.skill.AbstractSkill;
import io.gamioo.ioc.wrapper.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.util.List;


/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */

@DisplayName("IOC测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IOCFactoryTest {
    private static final Logger logger = LogManager.getLogger(IOCFactoryTest.class);
    //private final Benchmark=new Benchmark(10000);
    private static ConfigApplicationContext context;

    @BeforeAll
    public static void beforeAll() {
        //初始化......
        context = new ConfigApplicationContext(IOCFactoryTest.class.getPackage().getName());

    }

    @Test
    @Order(1)
    @DisplayName("容器扫描和获取")
    public void test() {
//        //初始化完毕，获取想要的bean
        RoleService roleService = context.getBean(RoleService.class);
        roleService.handleCommand("-- add rmb");
        List<AbstractSkill> list = roleService.getSkillList();
        logger.debug("array={}", list);

    }

    @Test
    @Order(2)
    @DisplayName("配置文件读取")
    public void handleConfig() {
        ServerConfig serverConfig = context.getBean(ServerConfig.class);
        logger.debug("serverConfig={}", serverConfig);

    }

    @Test
    @Order(3)
    @DisplayName("指令读取")
    public void handleControl() {
        Command command = context.getCommand(10001);
        logger.debug("command={}", command);

    }

    @BeforeEach
    public void beforeEach() {

    }

    @AfterEach
    public void afterEach() {

    }


    @AfterAll
    public static void afterAll() {

    }


}
