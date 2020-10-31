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

package io.gamioo.core.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 线程工具类.
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class ThreadUtils {
    private static final Logger logger = LogManager.getLogger(ThreadUtils.class);
    /**
     * 暂停执行.
     * <p>
     * 就是JDK的{@link Thread#sleep(long)}包装一下就不用管这个异常了.<br>
     * 这个方法只用于写一些测试用例时使用...
     *
     * @param millis 暂停毫秒数
     */
    public static void sleep(long millis) {
        try {
            if(millis>0){
                Thread.sleep(millis);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 输出当前线程正在运行的堆栈信息.
     *
     * @param thread 当前线程
     * @return 当前线程正在运行的堆栈信息
     */
    public static String printStackTrace(Thread thread) {
        final StackTraceElement[] st = thread.getStackTrace();
        StringBuffer sb = new StringBuffer(2048);
        sb.append("\n");
        for (StackTraceElement e : st) {
            sb.append("\tat ").append(e).append("\n");
        }
        return sb.toString();
    }

    /**
     * 输出当前线程正在运行的堆栈信息.
     * @return 当前线程正在运行的堆栈信息
     */
    public static void printStackTrace() {

        try{
            throw new Exception();
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }



    }
}