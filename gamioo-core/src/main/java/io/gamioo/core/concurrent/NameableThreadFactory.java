package io.gamioo.core.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂,可以设置别名
 *
 * @author Allen Jiang[41157121@qq.com]
 * @since 1.0
 */
public class NameableThreadFactory implements ThreadFactory {
    private final String name;
    private final AtomicInteger threadCounter = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable runnable) {
        StringBuilder threadName = new StringBuilder(name);
        threadName.append("-").append(threadCounter.getAndIncrement());
        Thread thread = new Thread(group, runnable, threadName.toString());
        return thread;
    }

    final ThreadGroup group;

    public NameableThreadFactory(String name) {
        SecurityManager securitymanager = System.getSecurityManager();
        this.group = securitymanager == null ? Thread.currentThread().getThreadGroup() : securitymanager.getThreadGroup();
        this.name = name;
    }
}
