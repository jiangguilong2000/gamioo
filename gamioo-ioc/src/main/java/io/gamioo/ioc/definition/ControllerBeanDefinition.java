package io.gamioo.ioc.definition;

import java.lang.annotation.Annotation;

/**
 * 控制器入口的定义
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class ControllerBeanDefinition extends GenericBeanDefinition{


    public ControllerBeanDefinition(Class<?> clazz, Annotation annotation) {
        super(clazz, annotation);
    }


}
