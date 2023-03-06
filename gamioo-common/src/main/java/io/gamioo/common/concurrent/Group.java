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

package io.gamioo.common.concurrent;

/**
 * 执行线程组枚举类.
 * <p>
 * 线程调度规则：N个线程 处理M个队列，哪个线程空闲了就去处理有任务的队列
 * </p>
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public enum Group {
    /**
     * 以玩家ID划分的线程.
     * <p>
     * 可以理解为一个玩家一个线程<br>
     */
    ModuleThreadGroup,

    /**
     * 以模块划分的线程.
     * <p>
     * 可以理解为一个Controller一个线程<br>
     * 如：登录，世界聊天，公会，排行榜
     */
    NettyThreadGroup,

    /**
     * Netty本身的Work线程.
     * <p>
     * 心跳，或完全没有IO操作的逻辑，直接交给Netty的Work线程处理掉
     */
    PlayerThreadGroup,

    /**
     * 一种串行执行队列处理线程.只会有一根线程来处理，
     */
    QueueThreadGroup;
}