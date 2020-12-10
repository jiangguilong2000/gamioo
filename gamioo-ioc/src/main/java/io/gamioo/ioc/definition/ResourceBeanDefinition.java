package io.gamioo.ioc.definition;

import java.lang.annotation.Annotation;

/**
 *  资源解析管理器的定义
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class ResourceBeanDefinition extends GenericBeanDefinition{
    public ResourceBeanDefinition(Class<?> clazz, Annotation annotation) {
        super(clazz, annotation);
    }
}
