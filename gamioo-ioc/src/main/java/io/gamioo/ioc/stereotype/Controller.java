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

package io.gamioo.ioc.stereotype;

import io.gamioo.common.concurrent.Group;

import java.lang.annotation.*;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Controller {
    /**模块*/
    String module() default "";
    /**
     * 标识这个协议控制中的入口方法由哪个线程组调用.
     * <p>
     * 默认转化为串型执行队列线程
     *
     * @return 执行线程组.
     */
    Group group() default Group.QueueThreadGroup;



}