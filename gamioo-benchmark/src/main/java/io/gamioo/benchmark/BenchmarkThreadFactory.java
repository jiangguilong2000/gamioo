package io.gamioo.benchmark;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 压力测试线程创建工厂.
 *
 * @author 江贵龙[41157121@qq.com]
 * @since 1.0
 */
public class BenchmarkThreadFactory implements ThreadFactory {
    private final AtomicInteger threadCounter = new AtomicInteger(0);
    private final String name;
    private final ThreadGroup group;

    public BenchmarkThreadFactory(String name) {
        SecurityManager securitymanager = System.getSecurityManager();
        this.group = securitymanager == null ? Thread.currentThread().getThreadGroup() : securitymanager.getThreadGroup();
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        StringBuilder threadName = new StringBuilder(name);
        threadName.append("-").append(threadCounter.getAndIncrement());
        Thread thread = new Thread(group, runnable, threadName.toString());
        return thread;
    }
}
