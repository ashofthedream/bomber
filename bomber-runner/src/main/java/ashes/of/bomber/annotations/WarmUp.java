package ashes.of.bomber.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


/**
 * Enable warm-up stage and defines settings for it
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface WarmUp {

    /**
     * @return test time
     */
    long time() default 60;

    /**
     * @return time unit for time
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * @return number of threads
     */
    int threads() default 1;

    /**
     * @return number of total invocations for all threads
     */
    long totalInvocations() default 1_000_000_000;

    /**
     * @return number of invocations per each thread.
     */
    long threadInvocations() default 1_000_000_000;


    boolean disabled() default false;
}
