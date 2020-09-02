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

package io.gamioo.ioc.factory.annotation;

import io.gamioo.ioc.config.BeanDefinition;
import io.gamioo.ioc.type.AnnotationMetadata;
import io.gamioo.ioc.type.MethodMetadata;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public interface AnnotatedBeanDefinition extends BeanDefinition {
    /**
     * Obtain the annotation metadata (as well as basic class metadata)
     * for this bean definition's bean class.
     * @return the annotation metadata object (never {@code null})
     */
    AnnotationMetadata getMetadata();

    /**
     * Obtain metadata for this bean definition's factory method, if any.
     * @return the factory method metadata, or {@code null} if none
     */
    MethodMetadata getFactoryMethodMetadata();
}
