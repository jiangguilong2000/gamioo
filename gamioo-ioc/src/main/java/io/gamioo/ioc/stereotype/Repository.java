package io.gamioo.ioc.stereotype;

import java.lang.annotation.*;

/**
 * Repository注解用来标识一个数据库处理类.
 *
 * @author Allen Jiang
 * @since 1.0.0
 */

@Component
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Repository {
}