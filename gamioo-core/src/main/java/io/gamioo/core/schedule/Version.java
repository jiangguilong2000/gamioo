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
package io.gamioo.core.schedule;

public class Version {
    /**
     * ----日期----版本-------更新说明------------------
     * 2016-11-03 2.2.0  重构代码，支持注解方式。
     * 2017-04-05 3.0.0  重构代码，兼容Spring的Scheduled注解。
     * 2019-04-25 3.5.0  支持提交到线程池中运行
     */
    public final static int MajorVersion = 3;
    public final static int MinorVersion = 5;
    public final static int RevisionVersion = 0;

    public static final String VERSION = Version.MajorVersion + "." + Version.MinorVersion + "." + Version.RevisionVersion;
    public static final String RELEASE_AT = "2019-04-25";
    public static final String RELEASE_NOTE = "注解不指定任何参数时，提交到线程池中运行";

    private Version() {
    }
}
