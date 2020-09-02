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

package io.gamioo.ioc.type.classreading;

import io.gamioo.ioc.type.AnnotationMetadata;
import io.gamioo.ioc.type.MethodMetadata;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class AnnotationMetadataReadingVisitor extends ClassMetadataReadingVisitor implements AnnotationMetadata {


    protected final ClassLoader classLoader;

    protected final Set<String> annotationSet = new LinkedHashSet<String>(4);

    protected final Map<String, Set<String>> metaAnnotationMap = new LinkedHashMap<String, Set<String>>(4);



    protected final Set<MethodMetadata> methodMetadataSet = new LinkedHashSet<MethodMetadata>(4);


    public AnnotationMetadataReadingVisitor(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }


    public Set<String> getAnnotationTypes() {
        return this.annotationSet;
    }

    public Set<String> getMetaAnnotationTypes(String annotationName) {
        return this.metaAnnotationMap.get(annotationName);
    }


    public boolean hasAnnotation(String annotationName) {
        return this.annotationSet.contains(annotationName);
    }
    @Override
    public boolean isAnnotated(String annotationName) {
        return true;
    }

}
