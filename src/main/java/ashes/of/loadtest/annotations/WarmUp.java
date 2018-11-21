package ashes.of.loadtest.annotations;

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
     * @return number of threads
     */
    int threads() default 1;

    /**
     * @return number of total iterations for all threads
     */
    long totalIterations() default Long.MAX_VALUE;

    /**
     * @return number of iterations per each thread.
     */
    long threadIterations() default Long.MAX_VALUE;

    /**
     * @return test time
     */
    long time() default 30;

    /**
     * @return time unit for time
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    boolean disabled() default false;
}
