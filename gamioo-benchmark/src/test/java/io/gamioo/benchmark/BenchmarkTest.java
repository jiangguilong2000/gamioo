/*
 * Copyright © 2020 www.gamioo.io All Rights Reserved.
 *
 * 感谢您选择gamioo框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合gamioo许可协议，否则不得使用该文件，您可以下载许可协议文件：
 *
 * 		http://www.gamioo.io/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归gamioo研发团队所有，我们保留所有权利;
 * 4.凡侵犯gamioo版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package io.gamioo.benchmark;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

/**
 * 测试用例.
 *
 * @author Allen Jiang
 * @since 1.0
 */
public class BenchmarkTest {
    private final Benchmark benchmark = new Benchmark(100000);

    @Test
    public void test() throws Exception {
        benchmark.run("random function test", () -> RandomUtils.nextInt(0, 1000));
    }
}