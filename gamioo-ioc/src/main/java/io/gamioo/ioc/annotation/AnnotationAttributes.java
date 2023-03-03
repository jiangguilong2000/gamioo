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

package io.gamioo.ioc.annotation;


import io.gamioo.common.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.LinkedHashMap;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class AnnotationAttributes extends LinkedHashMap<String, Object> {
    private final Class<? extends Annotation> annotationType;

    private final String displayName;


    /**
     * Create a new, empty {@link AnnotationAttributes} instance.
     */
    public AnnotationAttributes() {
        this.annotationType = null;
        this.displayName = "unknown";
    }

    /**
     * Create a new, empty {@link AnnotationAttributes} instance for the
     * specified {@code annotationType}.
     *
     * @param annotationType the type of annotation represented by this
     *                       {@code AnnotationAttributes} instance; never {@code null}
     * @since 4.2
     */
    public AnnotationAttributes(Class<? extends Annotation> annotationType) {
        Assert.notNull(annotationType, "annotationType must not be null");
        this.annotationType = annotationType;
        this.displayName = annotationType.getName();
    }

    /**
     * Get the type of annotation represented by this
     * {@code AnnotationAttributes} instance.
     *
     * @return the annotation type, or {@code null} if unknown
     * @since 4.2
     */
    public Class<? extends Annotation> annotationType() {
        return this.annotationType;
    }

    /**
     * Get the value stored under the specified {@code attributeName} as a
     * string.
     *
     * @param attributeName the name of the attribute to get; never
     *                      {@code null} or empty
     * @return the value
     * @throws IllegalArgumentException if the attribute does not exist or
     *                                  if it is not of the expected type
     */
    public String getString(String attributeName) {
        return getRequiredAttribute(attributeName, String.class);
    }

    /**
     * Get the value stored under the specified {@code attributeName},
     * ensuring that the value is of the {@code expectedType}.
     * <p>If the {@code expectedType} is an array and the value stored
     * under the specified {@code attributeName} is a single element of the
     * component type of the expected array type, the single element will be
     * wrapped in a single-element array of the appropriate type before
     * returning it.
     *
     * @param attributeName the name of the attribute to get; never
     *                      {@code null} or empty
     * @param expectedType  the expected type; never {@code null}
     * @return the value
     * @throws IllegalArgumentException if the attribute does not exist or
     *                                  if it is not of the expected type
     */
    @SuppressWarnings("unchecked")
    private <T> T getRequiredAttribute(String attributeName, Class<T> expectedType) {
        Assert.hasText(attributeName, "attributeName must not be null or empty");
        Object value = get(attributeName);
        if (!expectedType.isInstance(value) && expectedType.isArray() &&
                expectedType.getComponentType().isInstance(value)) {
            Object array = Array.newInstance(expectedType.getComponentType(), 1);
            Array.set(array, 0, value);
            value = array;
        }
        return (T) value;
    }
}
