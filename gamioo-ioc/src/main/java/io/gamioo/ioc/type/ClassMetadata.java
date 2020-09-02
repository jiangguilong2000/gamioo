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
public interface ClassMetadata {


    /**
     * Return the name of the underlying class.
     */
    String getClassName();

    /**
     * Return whether the underlying class represents an interface.
     */
    boolean isInterface();

    /**
     * Return whether the underlying class represents an annotation.
     * @since 4.1
     */
    boolean isAnnotation();

    /**
     * Return whether the underlying class is marked as abstract.
     */
    boolean isAbstract();

    /**
     * Return whether the underlying class represents a concrete class,
     * i.e. neither an interface nor an abstract class.
     */
    boolean isConcrete();

    /**
     * Return whether the underlying class is marked as 'final'.
     */
    boolean isFinal();

    /**
     * Determine whether the underlying class is independent,
     * i.e. whether it is a top-level class or a nested class
     * (static inner class) that can be constructed independent
     * from an enclosing class.
     */
    boolean isIndependent();

    /**
     * Return whether the underlying class has an enclosing class
     * (i.e. the underlying class is an inner/nested class or
     * a local class within a method).
     * <p>If this method returns {@code false}, then the
     * underlying class is a top-level class.
     */
    boolean hasEnclosingClass();

    /**
     * Return the name of the enclosing class of the underlying class,
     * or {@code null} if the underlying class is a top-level class.
     */
    String getEnclosingClassName();

    /**
     * Return whether the underlying class has a super class.
     */
    boolean hasSuperClass();

    /**
     * Return the name of the super class of the underlying class,
     * or {@code null} if there is no super class defined.
     */
    String getSuperClassName();

}
