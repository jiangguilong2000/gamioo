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

import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import io.gamioo.core.util.ClassUtils;
import io.gamioo.core.util.FieldUtils;
import io.gamioo.core.util.MethodUtils;
import io.gamioo.core.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class GenericBeanDefinition implements BeanDefinition {

    private static final Set<Class<?>> IGNORE_ANNOTATION_BY_METHODS = new HashSet<>();

    static {
        IGNORE_ANNOTATION_BY_METHODS.add(Deprecated.class);
    }

    // protected Object instance;
    private final Map<Class<? extends Annotation>, List<FieldDefinition>> fieldStore = new HashMap<>();
    protected Map<Class<? extends Annotation>, List<MethodDefinition>> methodStore = new HashMap<>();
    private final Class<?> beanClass;
    private final Annotation annotation;


    public GenericBeanDefinition(Class<?> clazz, Annotation annotation) {
        this.beanClass = clazz;
        this.annotation = annotation;
        this.analysisFieldList();
        this.analysisMethodList();
    }

    /**
     * 解析方法
     */
    @Override
    public void analysisMethodList() {
        MethodAccess access = MethodAccess.get(beanClass);
        List<Method> list = MethodUtils.getMethodList(beanClass);
        for (Method method : list) {
            Annotation[] array = method.getAnnotations();
            if (array != null && array.length > 0) {
                for (Annotation annotation : array) {
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    if (IGNORE_ANNOTATION_BY_METHODS.contains(annotationType)) {
                        continue;
                    }
                    this.analysisMethod(annotationType, access, method);
                }
            }
        }

    }


    private void analysisMethod(Class<? extends Annotation> annotationType, MethodAccess access, Method method) {
        methodStore.computeIfAbsent(annotationType, key -> new ArrayList<>(64)).add(new GenericMethodDefinition(access, method));
    }


    @Override
    public void analysisFieldList() {
        FieldAccess access = FieldAccess.get(beanClass);
        List<Field> list = FieldUtils.getFieldList(beanClass);
        for (Field field : list) {
            Annotation[] array = field.getAnnotations();
            for (Annotation annotation : array) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                this.analysisField(annotationType, access, field);
            }
        }
    }

    /**
     * 解析实体对象 异步事件等
     *
     * @param instance
     */
    @Override
    public void analysisBean(Object instance) {
//
    }


    private void analysisField(Class<? extends Annotation> annotationType, FieldAccess access, Field field) {

        Class<?> clazz = field.getType();
        if (clazz == List.class) {
            fieldStore.computeIfAbsent(annotationType, key -> new ArrayList<>()).add(new ListFieldDefinition(field));
        } else if (clazz == Map.class) {
            fieldStore.computeIfAbsent(annotationType, key -> new ArrayList<>()).add(new MapFieldDefinition(field));
        } else {
            fieldStore.computeIfAbsent(annotationType, key -> new ArrayList<>(64)).add(new GenericFieldDefinition(field));
        }
    }

//    @Override
//    public void inject(Object instance){
//    }

    @Override
    public List<FieldDefinition> getFieldDefinitionList(Class<? extends Annotation> clazz) {
        return fieldStore.getOrDefault(clazz, new ArrayList<>());
    }

    @Override
    public List<MethodDefinition> getMethodDefinitionList(Class<? extends Annotation> clazz) {
        return methodStore.getOrDefault(clazz, new ArrayList<>());
    }

    /**
     * 初始化的方法
     */
    @Override
    public MethodDefinition getInitMethodDefinition() {
        MethodDefinition ret = null;
        List<MethodDefinition> list = methodStore.get(PostConstruct.class);
        if (list != null && list.size() > 0) {
            ret = list.get(0);
        }
        return ret;
    }

    /**
     * 销毁的方法
     */
    public MethodDefinition getDestroyMethodDefinition() {
        MethodDefinition ret = null;
        List<MethodDefinition> list = methodStore.get(PreDestroy.class);
        if (list != null && list.size() > 0) {
            ret = list.get(0);
        }
        return ret;
    }

    @Override
    public Object newInstance() {
        return ClassUtils.newInstance(this.beanClass);
    }

//    /**
//     * 注入
//     */
//    @Override
//    public void inject() {
//        //   fieldList.values().forEach(v -> v.forEach(field -> field.inject(this)));
//
//    }
//
//    public List<FieldDefinition> getAutowiredFieldDefinitionList() {
//        List<FieldDefinition> list = fieldStore.getOrDefault(Autowired.class, new ArrayList<>());
//        return list;
//    }


//    public List<FieldDefinition> getValueFieldDefinition() {
//        List<FieldDefinition> list = fieldStore.get(Value.class);
//        return list;
//    }

    @Override
    public Class<?> getClazz() {
        return this.beanClass;
    }

    @Override
    public String getName() {
        return StringUtils.uncapitalized(this.beanClass.getSimpleName());
    }

    /**
     * 获取注解
     */
    @Override
    public Annotation getAnnotation() {
        return this.annotation;
    }


    /**
     * 获取注解类型
     */
    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return this.getAnnotation().annotationType();
    }

    /**
     * 获取此方法的访问入口所对应的Index.
     *
     * @return 访问入口所对应的Index.
     */
    @Override
    public int getIndex() {
        return 0;
    }
}
