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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;


/**
 * 注解工具类.
 *
 * @author Allen Jiang
 * @since 1.0.0
 */

public class AnnotationUtils {
    private AnnotationUtils() {
    }

    /**
     * 获取指定类型的注解或注解上有指定的注解.
     *
     * @param element        注解元素
     * @param annotationType 注解类型
     * @param <A>            注解类型
     * @return 返回标识有指定注解的注解
     */
    public static <A extends Annotation> Annotation getAnnotation(AnnotatedElement element, Class<A> annotationType) {
        A annotation = element.getAnnotation(annotationType);
        if (annotation == null) {
            for (Annotation metaAnn : element.getAnnotations()) {
                annotation = metaAnn.annotationType().getAnnotation(annotationType);
                if (annotation != null) {
                    return metaAnn;
                }
            }
        }
        return annotation;
    }
}
