package io.gamioo.common.schedule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 向Spring 调度器看齐
 *
 * @author Allen Jiang
 * @ClassName: Scheduled
 * @since V1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Scheduled {
    String cron() default "";

    int fixedDelay() default 0;

    int fixedRate() default 0;

    int initialDelay() default 0;
}
