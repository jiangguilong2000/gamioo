/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
 *
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 *
 * 		http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package io.gamioo.benchmark;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;

/**
 * 性能基准测试.
 *
 * @author Allen Jiang
 * @since 1.0
 */
public class Benchmark {
    private static final int WARM_UP_TIMES = 1000;
    private final int times;

    public Benchmark() {
        this(100_0000);
    }

    public Benchmark(int times) {
        this.times = times;
        System.out.println("Benchmark Test times:" + times + "\n");
    }

    public void run(String name, BenchmarkCallback callback) throws Exception {
        // 预热
        for (int i = 0; i < WARM_UP_TIMES; i++) {
            callback.handle();
        }

        // 计时，跑测试
        Instant startTime = Instant.now();
        for (int i = 0; i < times; i++) {
            callback.handle();
        }
        
        Instant endTime = Instant.now();
        long interval = Duration.between(startTime, endTime).toMillis();
        String  result=MessageFormat.format("{0} - {1} ms", name,String.valueOf(interval));
        System.out.println(result);
    }
}