/**
 * Copyright (c) 2015, 玛雅牛［李飞］
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gamioo.common.schedule;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.IPlugin;
import it.sauronsoftware.cron4j.Scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author Allen Jiang
 * @ClassName: SchedulerPlugin
 * @Description: 简单任务调度插件，整合了cron4j和ScheduledThreadPoolExecutor
 * @since V1.0.0
 */
public class SchedulerPlugin implements IPlugin {

    private static final Log LOG = Log.getLog("scheduler");

    /**
     * cron调度器
     */
    private Scheduler cronScheduler = null;

    /**
     * 调度线程池
     */
    private int scheduledThreadPoolSize = 10;

    /**
     * ScheduledThreadPoolExecutor调度器
     */
    private ScheduledThreadPoolExecutor fixedScheduler;

    /**
     * 执行器
     */
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * 调度任务配置文件
     */
    private final String jobConfigFile;

    /**
     * 扫描根目录
     */
    private String scanRootPackage = null;

    /**
     * 是否又cron任务
     */
    private boolean hasCronJob = false;

    /**
     * 是否有ScheduledThreadPoolExecutor任务
     */
    private boolean hasFixedJob = false;

    /**
     * <p>Title: SchedulerPlugin</p>
     * <p>Description: 构造函数(指定调度线程池大小、调度任务配置文件和扫描路径)</p>
     *
     * @param scheduledThreadPoolSize 调度线程池大小
     * @param jobConfigFile           调度任务配置文件
     * @param scanRootPackage         扫描路径
     * @since V1.0.0
     */
    private SchedulerPlugin(int scheduledThreadPoolSize, String jobConfigFile, String scanRootPackage) {
        this.scheduledThreadPoolSize = scheduledThreadPoolSize;
        this.jobConfigFile = jobConfigFile;
        this.scanRootPackage = scanRootPackage;
    }

    /**
     * @Title: ensureCronScheduler
     * @Description: 确保cronScheduler可用
     * @since V1.0.0
     */
    private void ensureCronScheduler() {
        if (this.cronScheduler == null) {
            synchronized (this) {
                if (this.cronScheduler == null) {
                    this.cronScheduler = new Scheduler();
                }
            }
        }
    }

    /**
     * @param job            定期执行的任务(Runnable)
     * @param cronExpression cron调度表达式
     * @Title: scheduleCron
     * @Description: 添加基于Linux下的crontab表达式的调度任务(Runnable)
     * @since V1.0.0
     */
    public void scheduleCron(Runnable job, String cronExpression) {
        ensureCronScheduler();
        this.hasCronJob = true;
        this.cronScheduler.schedule(cronExpression, job);
    }

    /**
     * @Title: ensurFixedScheduler
     * @Description: 确保fixedScheduler可用
     * @since V1.0.0
     */
    private void ensurFixedScheduler() {
        if (this.fixedScheduler == null) {
            synchronized (this) {
                if (this.fixedScheduler == null) {
                    this.fixedScheduler = new ScheduledThreadPoolExecutor(scheduledThreadPoolSize);
                }
            }
        }
    }

    /**
     * @param job                 定期执行的任务
     * @param initialDelaySeconds 启动延迟时间
     * @param periodSeconds       每次执行任务的间隔时间(单位秒)
     * @return
     * @Title: scheduleAtFixedRate
     * @Description: 延迟指定秒后启动，并以固定的频率来运行任务。后续任务的启动时间不受前次任务延时影响（并行）。
     * @since V1.0.0
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable job, int initialDelaySeconds, int periodSeconds) {
        ensurFixedScheduler();
        this.hasFixedJob = true;
        return fixedScheduler.scheduleAtFixedRate(job, initialDelaySeconds, periodSeconds, TimeUnit.SECONDS);
    }

    /**
     * @param job                 定期执行的任务
     * @param initialDelaySeconds 启动延迟时间
     * @param periodSeconds       每次执行任务的间隔时间(单位秒)
     * @return
     * @Title: scheduleWithFixedDelay
     * @Description: 延迟指定秒后启动，两次任务间保持固定的时间间隔(任务串行执行，前一个结束之后间隔固定时间后一个才会启动)
     * @since V1.0.0
     */
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable job, int initialDelaySeconds, int periodSeconds) {
        ensurFixedScheduler();
        this.hasFixedJob = true;
        return fixedScheduler.scheduleWithFixedDelay(job, initialDelaySeconds, periodSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean start() {
        //自动通过注解加载
   //     scanAnnotation();
        //通过文件记载
        loadJobsFromConfigFile();
        //启动cron任务
        if (this.hasCronJob) {
            this.cronScheduler.setDaemon(true);
            this.cronScheduler.start();
            LOG.info("CronScheduler已启动");
        }
        //启动fixed任务
        if (this.hasFixedJob) {
            LOG.info("ScheduledThreadPoolExecutor已启动");
        }
        return true;
    }

    @Override
    public boolean stop() {
        //停止cron任务
        if (this.hasCronJob) {
            this.cronScheduler.stop();
            this.cronScheduler = null;
            LOG.info("CronScheduler已停止");
        }
        //停止fixed任务
        if (this.hasFixedJob) {
            //暴力强制停止
            this.fixedScheduler.shutdownNow();
            this.fixedScheduler = null;
            LOG.info("ScheduledThreadPoolExecutor已停止");
        }
        //直接退出
        this.executor.shutdownNow();
        LOG.info("SchedulerPlugin已停止");
        return true;
    }

    /**
     * @Title: loadJobsFromConfigFile
     * @Description: 从配置文件汇总加载任务
     * @since V1.0.0
     */
    private void loadJobsFromConfigFile() {
        if (StrKit.isBlank(this.jobConfigFile)) {
            return;
        }
        // 获取job配置文件
        Prop jobProp = PropKit.use(this.jobConfigFile);
        // 获得所有任务名
        Set<String> jobNames = this.getJobNamesFromProp(jobProp);
        if (jobNames.isEmpty()) {
            return;
        }
        // 逐个加载任务
        for (String jobName : jobNames) {
            loadJob(jobProp, jobName);
        }
    }

    /**
     * @param jobProp job配置
     * @param jobName job名
     * @Title: loadJob
     * @Description: 加载一个任务
     * @since V1.0.0
     */
    private void loadJob(Prop jobProp, String jobName) {
        // 任务开关，默认开启
        Boolean enable = jobProp.getBoolean(jobName + ".enable", Boolean.FALSE);
        // 任务被禁用，直接返回
        if (!enable) {
            return;
        }

        // 创建要执行的任务
        Runnable runnable = createRunnableJob(jobName, jobProp.get(jobName + ".class"));
        String cron = jobProp.get(jobName + ".cron");
        int fixedRate = jobProp.getInt(jobName + ".fixedRate", 0);
        int fixedDelay = jobProp.getInt(jobName + ".fixedDelay", 0);
        int initialDelay = jobProp.getInt(jobName + ".initialDelay", 0);
        //参数检查
        int doubleCheckCounter = 0;
        if (!cron.isEmpty()) {
            doubleCheckCounter++;
            if (initialDelay != 0) {
                throw new RuntimeException("initialDelay只能与fixedDelay/fixedRate搭配使用，不能与cron搭配使用，jobName=" + jobName);
            }
        }
        if (fixedDelay != 0) {
            doubleCheckCounter++;
        }
        if (fixedRate != 0) {
            doubleCheckCounter++;
        }
        if (doubleCheckCounter != 1) {
            throw new RuntimeException(jobName + "的cron/fixedDelay/fixedRate需要且只能设定其中一个");
        }
        if (!cron.isEmpty()) {
            this.scheduleCron(runnable, cron);
            LOG.info("通过配置文件自动加载Cron类型定时任务( jobName=" + jobName + ",cron=" + cron + " )");
        } else {
            if (fixedDelay != 0) {
                this.scheduleAtFixedRate(runnable, initialDelay, fixedDelay);
                LOG.info("通过配置文件自动加载FixedRate类型定时任务( jobName=" + jobName + ", initialDelay=" + initialDelay + "'s, fixedDelay=" + fixedDelay + "'s )");
            } else {
                if (fixedRate != 0) {
                    this.scheduleWithFixedDelay(runnable, initialDelay, fixedRate);
                    LOG.info("通过配置文件自动加载FixedDelay类型定时任务( jobName=" + jobName + ", initialDelay=" + initialDelay + "'s, FixedDelay=" + fixedDelay + "'s )");
                }
            }
        }
    }

    /**
     * @param jobName      任务名
     * @param jobClassName 任务类名
     * @return Runnable对象
     * @Title: createRunnableJob
     * @Description: 创建任务
     * @since V1.0.0
     */
    private Runnable createRunnableJob(String jobName, String jobClassName) {
        if (jobClassName == null) {
            throw new RuntimeException("请设定 " + jobName + ".class");
        }

        Object temp = null;
        try {
            temp = Class.forName(jobClassName).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("无法实例化类: " + jobClassName, e);
        }

        Runnable job = null;
        if (temp instanceof Runnable) {
            job = (Runnable) temp;
        } else {
            throw new RuntimeException("无法实例化类: " + jobClassName
                    + "，该类必须实现Runnable接口");
        }
        return job;
    }

    /**
     * @param jobProp job配置
     * @return 任务名集合
     * @Title: getJobNamesFromProp
     * @Description: 获得所有任务名
     * @since V1.0.0
     */
    private Set<String> getJobNamesFromProp(Prop jobProp) {
        Map<String, Boolean> jobNames = new HashMap<String, Boolean>();
        for (Object item : jobProp.getProperties().keySet()) {
            String fullKeyName = item.toString();
            // 获得job名
            String jobName = fullKeyName.substring(0, fullKeyName.indexOf("."));
            jobNames.put(jobName, Boolean.TRUE);
        }
        return jobNames.keySet();
    }

//    /**
//     * @Title: scanAnnotation
//     * @Description: 扫描所有任务注解
//     * @since V1.0.0
//     */
//    private void scanAnnotation() {
//        if (StrKit.isBlank(scanRootPackage)) {
//            return;
//        }
//        Reflections reflections = new Reflections(new ConfigurationBuilder()
//                .addUrls(ClasspathHelper.forClass(Scheduled.class))
//                .forPackages(scanRootPackage.split(","))
//                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
//        );
//        autoLoadByAnnotation(reflections);
//    }
//
//    /**
//     * @param reflections
//     * @Title: autoLoadByAnnotation
//     * @Description: 扫描Scheduled任务
//     * @since V1.0.0
//     */
//    private void autoLoadByAnnotation(Reflections reflections) {
//        Set<Class<?>> cronTaskClasses = reflections.getTypesAnnotatedWith(Scheduled.class);
//        for (Class<?> mc : cronTaskClasses) {
//            if (!Runnable.class.isAssignableFrom(mc)) {
//                throw new RuntimeException(mc.getName() + " 必须实现Runnable接口");
//            }
//            Scheduled scheduled = mc.getAnnotation(Scheduled.class);
//            try {
//                Runnable runnable = (Runnable) mc.newInstance();
//                String cron = scheduled.cron();
//                int fixedRate = scheduled.fixedRate();
//                int fixedDelay = scheduled.fixedDelay();
//                int initialDelay = scheduled.initialDelay();
//
//                if (!cron.isEmpty()) {
//                    this.scheduleCron(runnable, cron);
//                    LOG.info("通过注解自动加载Cron类型定时任务( expr=" + cron + ",job= " + mc.getName() + " )");
//                } else {
//                    if (fixedDelay != 0) {
//                        this.scheduleAtFixedRate(runnable, initialDelay, fixedDelay);
//                        LOG.info("通过注解自动加载FixedRate类型定时任务( initialDelay=" + initialDelay + "'s, period=" + fixedDelay + "'s, job= " + mc.getName() + " )");
//                    } else {
//                        if (fixedRate != 0) {
//                            this.scheduleWithFixedDelay(runnable, initialDelay, fixedRate);
//                            LOG.info("通过注解自动加载FixedDelay类型定时任务( initialDelay=" + initialDelay + "'s, period=" + fixedRate + "'s, job= " + mc.getName() + " )");
//                        } else {
//                            //都是0，则启动线程任务,提交这个任务
//                            this.executor.submit(runnable);
//                            LOG.info("通过注解自动加载常规线程任务(job= " + mc.getName() + " )");
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                throw new RuntimeException(e.getMessage(), e);
//            }
//        }
//    }

    /**
     * @return
     * @Title: builder
     * @Description: 返回一个构建器
     * @since V1.0.0
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        /**
         * 调度线程池大小
         */
        private int scheduledThreadPoolSize;

        /**
         * 调度任务配置文件
         */
        private String jobConfigFile = null;

        /**
         * 扫描根目录
         */
        private String scanRootPackage = null;

        /**
         * <p>Title: Builder</p>
         * <p>Description: 默认构造函数</p>
         *
         * @since V1.0.0
         */
        public Builder() {
            this.scheduledThreadPoolSize = this.getBestPoolSize();
        }

        /**
         * @return
         * @Title: scheduledThreadPoolSize
         * @Description: 配置调度线程池大小
         * @since V1.0.0
         */
        public Builder scheduledThreadPoolSize(int size) {
            this.scheduledThreadPoolSize = size;
            return this;
        }

        /**
         * @return
         * @Title: enableConfigFile
         * @Description: 使能配置文件加载
         * @since V1.0.0
         */
        public Builder enableConfigFile(String configFile) {
            this.jobConfigFile = configFile;
            return this;
        }

        /**
         * @return
         * @Title: enableAnnotationScan
         * @Description: 使能注解扫描（进行类库检查）
         * @since V1.0.0
         */
        public Builder enableAnnotationScan(String rootPackage) {
            this.scanRootPackage = rootPackage;
            try {
                //自动扫描相关类库检查
                Class.forName("org.reflections.Reflections");
                Class.forName("com.google.common.collect.Sets");
                Class.forName("javassist.bytecode.annotation.Annotation");
            } catch (Exception e) {
                throw new RuntimeException("开启自动扫描加载定时任务需要Reflections、Guava、javassist类库，请导入相应的jar包");
            }
            return this;
        }

        /**
         * @return
         * @Title: getBestPoolSize
         * @Description: 获得调度线程池大小
         * @since V1.0.0
         */
        private int getBestPoolSize() {
            try {
                final int cores = Runtime.getRuntime().availableProcessors();
                // 每个核有8个调度线程
                return cores * 8;
            } catch (Throwable e) {
                return 8;
            }
        }

        /**
         * @return
         * @Title: build
         * @Description: 构建一个调度器插件
         * @since V1.0.0
         */
        public SchedulerPlugin build() {
            return new SchedulerPlugin(this.scheduledThreadPoolSize, this.jobConfigFile, this.scanRootPackage);
        }
    }
}
