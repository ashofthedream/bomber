package ashes.of.bomber.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


/**
 * Enable baseline stage and defines settings for it
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Baseline {

    /**
     * @return stage time
     */
    long time() default 30;

    /**
     * @return time unit for time
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;


    boolean disabled() default false;
}
