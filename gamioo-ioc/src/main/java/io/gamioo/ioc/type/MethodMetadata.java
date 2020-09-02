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

package io.gamioo.ioc.type;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public interface MethodMetadata extends AnnotatedTypeMetadata {

    /**
     * Return the name of the method.
     */
    String getMethodName();

    /**
     * Return the fully-qualified name of the class that declares this method.
     */
    String getDeclaringClassName();

    /**
     * Return the fully-qualified name of this method's declared return type.
     *
     */
    String getReturnTypeName();

    /**
     * Return whether the underlying method is effectively abstract:
     * i.e. marked as abstract on a class or declared as a regular,
     * non-default method in an interface.
     *
     * @since 4.2
     */
    boolean isAbstract();

    /**
     * Return whether the underlying method is declared as 'static'.
     */
    boolean isStatic();

    /**
     * Return whether the underlying method is marked as 'final'.
     */
    boolean isFinal();

    /**
     * Return whether the underlying method is overridable,
     * i.e. not marked as static, final or private.
     */
    boolean isOverridable();
}
