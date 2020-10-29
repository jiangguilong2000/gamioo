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

package io.gamioo.ioc.definition;

import java.lang.annotation.Annotation;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public interface Definition {

    Class<?> getClazz();

    String getName();

    /**获取注解*/
    <T extends Annotation> T  getAnnotation();

    /**
     * 获取此方法的访问入口所对应的Index.
     *
     * @return 访问入口所对应的Index.
     */
    int getIndex();

}
