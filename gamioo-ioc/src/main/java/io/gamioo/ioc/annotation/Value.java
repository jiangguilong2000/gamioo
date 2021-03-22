package io.gamioo.ioc.annotation;

import java.lang.annotation.*;

/**
 * 注入配置文件中的属性.
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})

public @interface Value {

    /**
     * 属性配置文件中的配置键值.
     *
     * @return Key.
     */
    String value();
}