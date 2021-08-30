package ashes.of.bomber.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Throttle {

    /**
     * @return number of permits per time interval
     */
    int threshold() default 1;

    /**
     * @return time interval
     */
    long time() default 1;

    /**
     * @return time unit for time
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * @return indicates that limiter will be shared around all test threads
     */
    boolean shared() default false;
}
