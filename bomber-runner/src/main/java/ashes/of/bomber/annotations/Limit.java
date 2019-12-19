package ashes.of.bomber.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Limit {

    /**
     * @return stage time
     */
    int count() default 1;

    /**
     * @return stage time
     */
    long time() default 1;

    /**
     * @return time unit for time
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
